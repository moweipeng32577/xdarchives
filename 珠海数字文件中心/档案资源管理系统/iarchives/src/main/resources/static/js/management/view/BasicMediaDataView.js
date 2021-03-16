/**
 * Created by yl on 2018/3/28.
 */
/**
 * 缩略图组件。
 * Comps.view.BasicDataView
 */
Ext.define('Management.view.BasicMediaDataView',{
    id: 'images-view',
    extend:'Ext.panel.Panel',    //继承panel
    xtype:'basicmediadataview',
    layout:'fit',

    acrossSelections:[],        //跨页选择集合，用于保存跨页选择数据

    //配置项开关start
    hasPageBar:true,            //分页栏
    hasSearchBar:true,          //搜索栏
    hasCloseButton:true,        //关闭按钮
    hasCancelButton:true,       //取消选择按钮
    //配置项开关end

    //检索字段数据源
    searchstore:null,

    //数据来源，用于填充基础查询条件
    dataParams:{},
    datastore:null,
    address:'',
    //用来隐藏修改和删除按钮
    hmodify:false,
    hdelete:false,
    /**
     * 渲染控件
     */
    initComponent:function(){
        var me = this;
        //构造分页栏
        if(me.hasPageBar){ me.initPageBar(); }
        //构造查询栏
        if(me.hasSearchBar){
            if(me.searchstore == null){
                throw new Error('grid未设置检索字段searchstore!如不需要检索栏,请设置hasSearchBar:false');
            }
            me.initSearchBar();
        }
        if(me.datastore==null){
            throw new Error('请配置dataview的store数据源,fields集合必须有name、url');
            //构造主页面(dataview)
        }else{
            me.initItem();
        }
        me.callParent();
    },

    // setTitle:function(src){
    //     if(!src || src == ''){
    //         return;
    //     }
    //     var me = this;
    //     if(me.header && !me.header.height){
    //         me.header.setConfig('height',35);
    //         me.header.setConfig('padding', 5);
    //         me.header.setTitle(src);
    //     }else if(me.header && me.header.setTitle){
    //         me.header.setTitle(src);
    //     }else{
    //         me.header = {
    //             height : 35,
    //             padding : '5 5 5 10',
    //             title : src
    //         }
    //     }
    // },
    initItem:function(){
        var me = this;
        me.items = {
            xtype: 'dataview',
            itemId:'dataview',
            reference: 'dataview',
            trackOver: true,
            overItemCls: 'x-item-over',
            draggable:false,
            autoScroll:true,
            plugins: [
                {
                    ptype: 'ux-animated-dataview'
                },
                Ext.create('Ext.ux.DataView.DragSelector', {}),
                Ext.create('Ext.ux.DataView.LabelEditor', {dataIndex: 'name'})
            ],

            prepareData: function (data) {
                var container = me.down('fieldcontainer');
                var content = container.down('searchfield').getValue();
                var dataName;
                if(typeof content == 'undefined' || content == ''){
                    dataName = data.name;
                }else{
                    var reTag = /<(?:.|\s)*?>/g;
                    var value = (data.name+"").replace(reTag, "");
                    var reg = new RegExp(content, 'g');
                    dataName = value.replace(reg, function (match) {
                        return '<span style="color:red">' + match + '</span>'
                    });
                }
                Ext.apply(data, {
                    shortName: Ext.util.Format.ellipsis(dataName),
                    entryid: Ext.util.Format.ellipsis(data.id),
                    isPrint: Ext.util.Format.ellipsis(data.isprint),
                    sizeString: Ext.util.Format.fileSize(data.size)
                    // dateString: Ext.util.Format.date(data.lastmod, "m/d/Y g:i a")
                });
                return data;
            },
            selectionModel: {
                mode: 'MULTI'
            },
            itemSelector: 'div.thumb-wrap',

            tpl: [
                '<tpl for=".">',
                me.address !== "video" ?
                    '<div class="thumb-wrap" onmouseover="imageOver(select{entryid},edit{entryid})" onmouseout="imageout(select{entryid},edit{entryid})">'
                    : '<div class="thumb-wrap" id="wrap{entryid}" onmouseenter="imageEnter(videoPre{entryid},old{entryid},\'{url}\')" onmouseleave="imageLeave(videoPre{entryid},old{entryid},\'{url}\')" onmouseover="imageOver(select{entryid},edit{entryid},cover{entryid})" onmouseout="imageout(select{entryid},edit{entryid},cover{entryid})">',

                //原缩略图
                '<div id="old{entryid}"><div class="thumb" ><div class="block"><div style="width: 100%;font-size: 0;"><div class="panel">',
                '<img id="thumb{entryid}" src="/thematicProd/getBackground?address=' + me.address + '&url={url}"/>',
                me.address !== "video" ? "" : '<img id="cover{entryid}" class="cover" src="/img/defaultMedia/' + me.address + 'Cover.png" >',
                '</div><div style="width: 100%;height: 10px;"></div></div></div></div>',
                '<tpl if="printornot(isPrint)">',
                '<p style="color:blue;text-overflow:ellipsis;overflow: hidden;white-space: nowrap;margin: 0 10px;">{shortName}</p>',
                '<tpl else>',
                '<p style="text-overflow:ellipsis;overflow: hidden;white-space: nowrap;margin: 0 10px;">{shortName}</p>',
                '</tpl>',
                '</div>',

                me.address !== "video" ? "" ://浏览视频缩略图
                    '<div id="videoPre{entryid}" style="display: none" onmouseover="imageShow(moveThumb{entryid},thumb{entryid})" onmousemove="imageMove(event,this,moveThumb{entryid},thumb{entryid},rate{entryid})">' +
                    '<div class="thumb"><div class="block"><div style="width: 100%;"><div style="display: flex;align-items:center;justify-content: center;"><div class="panel">' +
                    '<div id="moveThumb{entryid}" style="background-position:center 0px;background-image: url(/thematicProd/getBackground?address=' + me.address + '&url={url}&isCombined=1);"></div>' +
                    '</div></div>' +
                    '<div style="width: 100%;height: 10px;border-color: #191919;border-style: solid;border-width: 0px 0px 7px 0px;background: #444;box-sizing: border-box;"><span id="rate{entryid}" style="display: block;background: #15b0ff;height: 3px;width: 0;"></span></div>' +
                    '</div></div></div>' +
                    '<p style="text-overflow:ellipsis;overflow: hidden;white-space: nowrap;margin: 0 10px;">{shortName}</p>' +
                    '</div>',

                //勾选按钮、菜单按钮
                '<img id="select{entryid}" '+ (me.address !== "video" ?'':'onmouseover="imageShow(moveThumb{entryid},thumb{entryid})"')+' onclick="clickSelect(select{entryid})" src="/img/defaultMedia/uncheck.png" style="cursor: pointer;display: none; position: absolute;top: 3px;left:3px;width: 17px;height:17px"/>',
                '<img id="edit{entryid}" onmouseover="editOver(edit{entryid}'+(me.address !== "video" ?'':',moveThumb{entryid},thumb{entryid}')+')" onmouseout="editOut(edit{entryid})" src="/img/defaultMedia/mediaMenu.png" style="cursor: pointer;display: none; position: absolute;top: 3px;right:3px;width: 20px;height:20px"/>',

                '</div>',
                '</tpl>',

                '<div class="x-clear"></div>',
                '<div id="plusDiv" style="display: none; top:0;right:0;left:0;bottom:0;margin:auto position: absolute;"><img id="plusImg" src="" alt="预览" /></div>'
            ],

            listeners: {
                itemclick: function (view, record, it, index, e, eOpts) {
                    var curItem = document.getElementById('select' + record.id);
                    if (typeof e.target.nodeName !== 'undefined' && e.target.nodeName.indexOf('IMG') !== -1 && typeof e.target.id !== 'undefined') {
                        if (e.target.id.indexOf('select') !== -1) {
                            if (curItem.src.indexOf('uncheck') !== -1) {
                                view.getSelectionModel().deselect(record);
                                Ext.Array.remove(me.acrossSelections, record);
                            } else {
                                view.getSelectionModel().select(record, true);//加上true,不影响其他选择
                                // Ext.Array.push(me.acrossSelections, record);
                            }
                            return false;
                        } else if (e.target.id.indexOf('edit') !== -1) {
                            if (curItem.src.indexOf('uncheck') !== -1) {
                                view.getSelectionModel().select(record);
                                me.acrossSelections = [];
                                Ext.Array.push(me.acrossSelections, record);
                            }
                            view.fireEvent("itemcontextmenu", view, record, it, index, e, eOpts);
                            return false;//截停该事件后续渲染
                        }
                    }

                    for (var i = 0; i < me.acrossSelections.length; i++) {
                        document.getElementById('select' + me.acrossSelections[i].id).style.display = "none";
                        document.getElementById('select' + me.acrossSelections[i].id).src = "/img/defaultMedia/uncheck.png";
                        document.getElementById('edit' + me.acrossSelections[i].id).style.display = "none";
                    }
                    curItem.src = "/img/defaultMedia/checked.png";
                    curItem.style.display = "";
                    document.getElementById('edit' + record.id).style.display = "";
                },
                // itemcontextmenu: function (view, record, item, index, e, eOpts) {
                //     e.preventDefault();
                //     if(!view.isSelected(item)){//未选中时触发选中事件，防止死循环
                //         view.fireEvent("itemclick", view, record, item, index, e, eOpts);
                //     }
                //     var dataViewMenu = Ext.create('Comps.view.DataViewMenu',{
                //         record:record,
                //         menuView:view
                //     }).showAt(e.getXY());
                //     if (me.address !== "photo") {
                //         dataViewMenu.remove(dataViewMenu.down('[itemId=previewItem]'));
                //     }
                //     if(me.hmodify){
                //         dataViewMenu.remove(dataViewMenu.down('[itemId=modifyMedia]'))
                //     }
                //     if(me.hdelete){
                //         dataViewMenu.remove(dataViewMenu.down('[itemId=delMedia]'))
                //     }
                // },
                selectionchange: function (dv, items) {
                    for (var i = 0; i < me.acrossSelections.length; i++) {
                        document.getElementById('select' + me.acrossSelections[i].id).src = "/img/defaultMedia/uncheck.png";
                        document.getElementById('select' + me.acrossSelections[i].id).style.display = "none";
                        document.getElementById('edit' + me.acrossSelections[i].id).style.display = "none";
                    }
                    for (var i = 0; i < items.length; i++) {
                        document.getElementById('select' + items[i].id).src = "/img/defaultMedia/checked.png";
                        document.getElementById('select' + items[i].id).style.display = "";
                        document.getElementById('edit' + items[i].id).style.display = "";
                    }

                    me.acrossSelections = [];
                    Ext.Array.push(me.acrossSelections, items);
                },
                itemdblclick: function (view, item) {
                }
            },
            store: me.datastore
        };
    },
    /**
     * 初始化分页栏，包括分页大小选择下拉框
     */
    initPageBar:function(){
        var me = this;
        me.bbar = {
            xtype: 'pagingtoolbar',
            displayInfo: true,
            plugins: 'ux-progressbarpager',
            store:me.datastore,
            items:{ //添加分页选择下拉框
                xtype:'combo',
                store: new Ext.data.ArrayStore({
                    fields: ['text', 'value'],
                    data: [['5', 5], ['10', 10], ['20', 20], ['50', 50], ['100', 100], ['300', 300]]
                }),
                value:XD.pageSize,//使用默认分页大小
                valueField: 'value',
                displayField: 'text',
                width: 80,
                editable: false,
                listeners:{
                    change:function(field,newvalue,oldvalue){
                        //选择分页大小，重新加载列表数据
                        var gridstore = me.down('[itemId=dataview]').getStore();
                        gridstore.setPageSize(newvalue);//设置列表store的大小
                        gridstore.loadPage(1);//重新加载第一页数据
                    }
                }
            },
            listeners:{
                beforechange:function(el, page, eopts){
                    var gridstore = me.down('[itemId=dataview]').getStore();
                    gridstore.paging = true;
                }
            }
        }
    },

    /**
     * 初始化搜索栏，包括在结果中搜索、搜索后标记搜索内容为红色
     */
    initSearchBar:function(){
        var me = this;
        var item = [{
            dock:'top',
            xtype:'fieldcontainer',
            height:40,
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            defaults:{
                margin:'0 2 0 0',
                xtype:'combo'
            },
            style:{ 'background':'#FFF' },
            items:[{
                width: 250,
                itemId: 'condition',
                labelWidth: 100,
                fieldLabel:'检索条件',
                labelSeparator:'：',
                labelAlign:'right',
                store:Array.isArray(me.searchstore)?{data:me.searchstore}:me.searchstore,
                queryMode:'local',
                valueField:'item',
                displayField:'name',
                listeners: {
                    //搜索条件默认选择第一项
                    afterrender: function (combo) {
                        var store = combo.getStore();
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                        }
                    }
                }
            }, {
                width: 120,
                itemId: 'operator',
                store: [['like','类似于'],['equal','等于'],['greaterThan','大于'],['lessThan','小于']],
                value: 'like'
            }, {
                xtype: 'searchfield',
                tooltip:'检索',
                width: 200,
                itemId: 'value',
                listeners:{
                    search:function(field){
                        //获取检索框的值
                        var container = me.down('fieldcontainer');
                        var condition = container.down('[itemId=condition]').getValue();
                        var operator = container.down('[itemId=operator]').getValue();
                        var content = field.getValue();
                        //检索数据
                        //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
                        var gridstore = me.down('[itemId=dataview]').getStore();
                        var searchcondition = condition;
                        var searchoperator = operator;
                        var searchcontent = content;
                        var inresult = me.down('[itemId=inresult]').getValue();
                        if(inresult){
                            var params = gridstore.getProxy().extraParams;
                            if(typeof(params.condition) != 'undefined'){
                                searchcondition = [params.condition,condition].join(XD.splitChar);
                                searchoperator = [params.operator,operator].join(XD.splitChar);
                                searchcontent = [params.content,content].join(XD.splitChar);
                            }
                        }
                        Ext.apply(gridstore.getProxy().extraParams, {
                            condition: searchcondition,
                            operator: searchoperator,
                            content: searchcontent
                        });
                        gridstore.loadPage(1);
                    }
                }
            }, {
                xtype: 'checkbox',
                itemId:'inresult',
                boxLabel: '结果中检索'
            }, {
                xtype: 'button',
                itemId:'gridList',
                margin:'0 0 0 5',
                text:'<span style="color: #000000 !important;">列表显示</span>',
                tooltip:'列表显示',
                style:{
                    'background-color':'#f6f6f6 !important',
                    'border-color':'#e4e4e4 !important'
                }
            }]
        },{
            xtype:'splitbutton',
            height:1,
            style:{ 'border-color':'#d0d0d0 !important' }
        }];
        var cancelSelected={
            xtype:'button',
            itemId:'basicgridCancelChooseBtn',
            margin:'0 0 0 5',
            //iconCls:'x-search-cancel-icon',
            text:'<span style="color: #000000 !important;">取消选择</span>',
            tooltip:'取消所有跨页选择项',
            style:{
                'background-color':'#f6f6f6 !important',
                'border-color':'#e4e4e4 !important'
            },
            handler:function(){
                for(var i = 0; i < me.down('[itemId=dataview]').getStore().getCount(); i++){
                    me.down('[itemId=dataview]').getSelectionModel().deselect(me.down('[itemId=dataview]').getStore().getAt(i));
                }
                me.acrossSelections = [];
            }
        };
        var close={
            xtype:'button',
            itemId:'basicgridCloseBtn',
            margin:'0 0 0 5',
            //iconCls:'x-search-close-icon',
            text:'<span style="color: #000000 !important;">关闭</span>',
            tooltip:'关闭当前页面',
            style:{
                'background-color':'#f6f6f6 !important',
                'border-color':'#e4e4e4 !important'
            },
            handler:function(){
                parent.closeObj.close(parent.layer.getFrameIndex(window.name));
            }
        };
        if(me.hasCancelButton){
            item[0].items.push(cancelSelected);
        }
        if(me.hasCloseButton){
            item[0].items.push(close);
        }
        me.dockedItems = item;
    },
    //--------------------------------------------提供外部使用函数---------------------------------------------------
    /**
     * 初始化列表数据
     * @param dataParams 列表基础过滤条件，不受搜索栏影响的内容。如{organid:'0'}
     * @returns {basicgrid} 当前列表
     */
    initGrid:function(dataParams){
        var me = this;

        //初始化列表数据
        if(typeof(dataParams) != 'undefined'){
            me.dataParams = dataParams
        }

        var store = me.down('[itemId=dataview]').getStore();
        Ext.apply(store.getProxy(),{
            extraParams:me.dataParams
        });
        store.loadPage(1);
        me.fireEvent('render',store);

        //初始化分页栏
        if(me.hasPageBar){
            me.down('pagingtoolbar combo').reset();
        }
        //初始化搜索框数据
        var field = me.down('searchfield');
        if(field != null){
            field.reset();
            //如果搜索框配置的是store，加载store数据
            if(!Array.isArray(me.searchstore)){
                var combo = me.down('[itemId=condition]');
                var conditionstore = combo.getStore();
                Ext.apply(conditionstore.proxy, {
                    extraParams:me.dataParams
                });
                conditionstore.load({callback:function(){
                    if (conditionstore.getCount() > 0) {
                        combo.select(conditionstore.getAt(0));
                    }
                }});
            }
        }
        return me;
    },
    /**
     * 删除数据后刷新列表数据。
     * 处理删除最后一页数据，回读上一页的问题
     * @param delcount 删除的数据量
     * @param fn 回掉函数，数据刷新成功后执行
     */
    delReload: function (delcount, fn) {
        var me = this;

        me.acrossSelections = [];
        me.acrossDeSelections = [];
        this.lastSelected = null;

        var store = me.down('dataview').getStore();
        var loadpage = store.currentPage;
        if (delcount > store.getCount()) {
            //跨页多选删除，计算删除的页数
            var totalPageCount = Math.ceil(store.getTotalCount() / store.pageSize);
            var delPageCount = Math.ceil(delcount / store.pageSize);
            loadpage = 1;
            if (delPageCount < totalPageCount) {
                loadpage = totalPageCount - delPageCount;
            }
        } else if (delcount == store.getCount()) {
            //删除当前页所有数据
            //如果不是最后一页，则刷新当前页
            //如果是最后一页且不是第一页，则读取上一页数据
            var totalPageCount = Math.ceil(store.getTotalCount() / store.pageSize);
            if (totalPageCount == store.currentPage && store.currentPage > 1) {
                loadpage = store.currentPage - 1;
            }
        }
        store.loadPage(loadpage, {callback: fn});
    },
    //清除选择项
    clearDataViewSelected: function () {
        var me = this;
        me.down('[itemId=dataview]').getSelectionModel().deselectAll();
        me.acrossSelections = [];
    },
    reloadDataView: function () {
        var me = this;
        var store = me.down('[itemId=dataview]').getStore();
        store.reload();
        me.down('[itemId=dataview]').refresh();//否则再次拖拉选取会异常
    },
    notResetInitGrid: function (dataParams) {
        var me = this;
        me.clearDataViewSelected();//清除选择项
        var store = me.down('[itemId=dataview]').getStore();
        //初始化列表数据
        if (typeof(dataParams) != 'undefined') {
            me.dataParams = dataParams;
            Ext.apply(store.getProxy(), {
                extraParams: me.dataParams
            });
        }
        me.reloadDataView();//重新加载
        // if (me.down('[itemId=selectAll]') != null) {
        //     me.down('[itemId=selectAll]').setValue(false);
        // }
    }
});
// function imageover(imgid, obj, imgbig) {
//     var maxwidth = 1000;
//     var maxheight = 1000;
//     obj.style.display = "";
//     imgbig.src = imgid.src;
//     //1、宽和高都超过了，看谁超过的多，谁超的多就将谁设置为最大值，其余策略按照2、3
//     //2、如果宽超过了并且高没有超，设置宽为最大值
//     //3、如果宽没超过并且高超过了，设置高为最大值
//
//     if (imgbig.width > maxwidth && imgbig.height > maxheight) {
//         var pare = (imgbig.width - maxwidth) - (imgbig.height - maxheight);
//         if (pare >= 0)
//             imgbig.width = maxwidth;
//         else
//             imgbig.height = maxheight;
//     } else if (imgbig.width > maxwidth && imgbig.height <= maxheight) {
//         imgbig.width = maxwidth;
//     } else if (imgbig.width <= maxwidth && imgbig.height > maxheight) {
//         imgbig.height = maxheight;
//     }
// }
function previewImage(record) {
    var plusDiv = document.getElementById('plusDiv');
    var maxwidth = 1600;
    var maxheight = 600;
    var size = '';
    var thumb = document.getElementById('thumb' + record.id);
    if (thumb.naturalWidth > maxwidth && thumb.naturalHeight > maxheight) {
        if ((thumb.naturalWidth / thumb.naturalHeight) > (maxwidth / maxheight)) {
        // if ((thumb.naturalWidth - maxwidth) - (thumb.naturalHeight - maxheight) >= 0) {
            size = "max-width:" + maxwidth;
        } else {
            size = "max-height:" + maxheight;
        }
    } else if (thumb.naturalWidth > maxwidth && thumb.naturalHeight <= maxheight) {
        size = "max-width:" + maxwidth;
    } else if (thumb.naturalWidth <= maxwidth && thumb.naturalHeight > maxheight) {
        size = "max-height:" + maxheight;
    }
    return "<img src='" + document.getElementById('thumb' + record.id).src + "' " + (size !== '' ? ("style='" + size + "px' />") : "/>");
}

function printornot(isPrint) {
    return isPrint == '是' ;
}
function imageout(selectobj, editobj, coverobj) {
    if(selectobj.length > 1&& editobj.length >1) {
        if (selectobj[0].src && selectobj[0].src.indexOf('uncheck') !== -1) {
            selectobj[0].style.display = "none";
            editobj[0].style.display = "none";
            if (coverobj) {
                coverobj.style.display = "";
            }
        }
    }
    else{
        if (selectobj.src && selectobj.src.indexOf('uncheck') !== -1) {
            selectobj.style.display = "none";
            editobj.style.display = "none";
            if (coverobj) {
                coverobj.style.display = "";
            }
        }
    }
}
function imageOver(selectobj, editobj, coverobj) {
    if (selectobj && editobj) {
        if(selectobj.length > 1&& editobj.length >1) {
            if (typeof selectobj[0].style !== 'undefined') {//刷新后立即onmouseover,不知道为何selectobj是HTMLCollection，暂时规避
                selectobj[0].style.display = "";
                editobj[0].style.display = "";
                if (coverobj) {
                    coverobj.style.display = "none";
                }
            }
        }
        else{
            if (typeof selectobj.style !== 'undefined') {//刷新后立即onmouseover,不知道为何selectobj是HTMLCollection，暂时规避
                selectobj.style.display = "";
                editobj.style.display = "";
                if (coverobj) {
                    coverobj.style.display = "none";
                }
            }
        }
    }
}
var timer;
function imageEnter(videoPreObj, oldObj, url) {
    if (url !== "" && typeof oldObj.style !== 'undefined') {//暂时规避
        timer = setTimeout(function () {
            oldObj.style.display = 'none';
            videoPreObj.style.display = '';
        }, 800);
    }
}
function imageLeave(videoPreObj, oldObj, url) {
    if (url !== "" && typeof oldObj.style !== 'undefined') {//暂时规避
        oldObj.style.display = '';
        videoPreObj.style.display = 'none';
        clearTimeout(timer);
    }
}
function imageMove(event, wrapobj, moveThumb, thumb, rate) {
    var count = 21;
    var offetX = event.clientX - getOffsetLeft(wrapobj);// document.all("rela").innerHTML = "X:"+offetX;
    rate.style.width = Math.round(offetX / wrapobj.offsetWidth * 10000) / 100 + "%";//进度条
    var num = count - 1;//最后一个像素也属于最后一张
    if (offetX / wrapobj.offsetWidth !== 1) {
        num = offetX / wrapobj.offsetWidth * count;
    }

    var thumbHight = thumb.height;
    if (thumb.naturalWidth / thumb.naturalHeight < 130 / 110) {
        thumbHight = 110;
        moveThumb.style.backgroundSize = Math.floor(130 * 110 / thumb.naturalHeight * 100) / 100 + "px";
    }
    moveThumb.style.backgroundPosition = "center -" + Math.floor(num) * thumbHight + "px";
}
function imageShow(moveThumb, thumb) {
    if (thumb.naturalWidth / thumb.naturalHeight < 130 / 110) {
        moveThumb.style.height = "110px";
        moveThumb.style.width = Math.floor(130 * 110 / thumb.naturalHeight * 100) / 100 + "px";
    } else {
        moveThumb.style.height = thumb.height + "px";
        moveThumb.style.width = "130px";
    }
}
function clickSelect(selectobj) {
    if(selectobj.length > 1){
        selectobj[0].src = selectobj[0].src.indexOf('uncheck') !== -1 ? "/img/defaultMedia/checked.png" : "/img/defaultMedia/uncheck.png";
    }
    else{
        selectobj.src = selectobj.src.indexOf('uncheck') !== -1 ? "/img/defaultMedia/checked.png" : "/img/defaultMedia/uncheck.png";
    }
}
function editOver(editobj, moveThumb, thumb) {
    editobj.src = "/img/defaultMedia/editOver.png";
    if (moveThumb && thumb) {
        imageShow(moveThumb, thumb);
    }
}
function editOut(editobj) {
    editobj.src = "/img/defaultMedia/mediaMenu.png";
}
function getOffsetLeft(obj) {//获取div的绝对路径
    var tmp = obj.offsetLeft;
    var val = obj.offsetParent;
    while (val !== null) {
        tmp += val.offsetLeft;
        val = val.offsetParent;
    }
    return tmp;
}
function isGroup() {
    if(dataType==='5'||dataType==='6'||dataType==='7'){
        return true;
    }
}

Ext.define('Comps.view.DataViewMenu', {
    extend: 'Ext.menu.Menu',
    margin: '0 0 0 0',
    minWidth: 80,
    xtype: 'dataViewMenu',
    itemId: 'dataViewMenuItem',
    items: [{
        text: '预览',
        itemId: 'previewItem',
        iconCls: 'x-ctxmenu-enlarge-icon',
        handler: function (me) {
            Ext.create('Ext.window.Window', {
                modal: true,
                header: false,
                bodyStyle: "background-color:#f6f6f6;padding:4px 4px 0px 4px",
                resizable: false,
                html: previewImage(me.findParentByType('dataViewMenu').record),
                listeners: {
                    maskclick: function (me) {
                        me.close();
                    }
                }
            }).show();
        }
    }, {
        text: "修改",
        itemId: 'modifyMedia',
        iconCls: 'x-ctxmenu-edit-icon'
    }, {
        text: "删除",
        itemId: 'delMedia',
        iconCls: 'x-ctxmenu-delete-icon'
    }, {
        text: "查看",
        itemId: 'lookMeida',
        iconCls: 'x-ctxmenu-eye-icon'
    }]
});