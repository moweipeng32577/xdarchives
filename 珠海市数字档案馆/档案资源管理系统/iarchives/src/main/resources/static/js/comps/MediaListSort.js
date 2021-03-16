/**
 * 媒体Tab展示公共组件
 */

Ext.define('Comps.view.MediaListSort', {
    extend: 'Ext.tab.Panel',
    xtype:'MediaListSort',
    hasPageBar:true,            //是否有分页栏
    hasSearchBar:true,          //是否有搜索栏
    photourl:'',                //图片请求地址
    dataurl:'',                 //列表请求地址
    itembuttons:[],             //按钮组
    nodeid:'',                  //节点id
    mediaType:'',                  //节点媒体类型
    params:null,                //缓存参数
    selectedItem:[],             //图片列表已选中元素数组
    requires: [
        'Ext.ux.TabReorderer'
    ],
    plugins: 'tabreorderer',

    defaults: {
        scrollable: true,
        closable: true
    },

    /**
     * condition: searchcondition,
     operator: searchoperator,
     content: searchcontent
     * @param nodeid
     */

    initTabs:function(params,isCheckFind){
        var tabPanel = this;

        if(params.nodeid&&tabPanel.hasSearchBar){//检索栏
            var cond = tabPanel.down('fieldcontainer').getComponent('condition');
            cond.getStore().proxy.extraParams.nodeid = params.nodeid?params.nodeid:tabPanel.params.nodeid;
            cond.getStore().load({callback:function(){
                if (cond.getStore().getCount() > 0) {
                    cond.select(cond.getStore().getAt(0));
                }
            }});
        }

        params.mediaType = params.mediaType?params.mediaType:tabPanel.params.mediaType;
        for(var i=0;i<tabPanel.items.length;i++){
            if(tabPanel.items.get(i).xtype!='GridView'){
                var store = tabPanel.items.get(i).down('dataview').getStore();
                store.proxy.extraParams.nodeid = params.nodeid?params.nodeid:tabPanel.params.nodeid;
                if(params){
                    params.content = params.content?params.content:'';
                    if(!isCheckFind){
                        for(var field in params){
                            if(store.proxy.extraParams[field]){
                                delete store.proxy.extraParams[field] ;
                            }
                        }
                    }
                   for(var field in params){
                       //Object.hasOwnProperty("name")
                       if(store.proxy.extraParams.hasOwnProperty(field)&&field!='mediaType'&&field!='nodeid'){
                           store.proxy.extraParams[field] += '#_#'+params[field];
                       }else{
                           store.proxy.extraParams[field] = params[field];
                       }
                   }
                }else{
                    for(var field in params){
                        if(store.proxy.extraParams[field]){
                            delete store.proxy.extraParams[field] ;
                        }
                    }
                }
                tabPanel.items.get(i).down('pagingtoolbar').setStore(store);
                store.loadPage(1);
            }
            if(tabPanel.items.get(i).xtype=='GridView'){
                if(params){
                    params.content = params.content?params.content:'';
                    if(!isCheckFind){
                        for(var field in params){
                            if(tabPanel.items.get(i).getStore().proxy.extraParams&&tabPanel.items.get(i).getStore().proxy.extraParams[field]){
                                delete tabPanel.items.get(i).getStore().proxy.extraParams[field] ;
                            }
                        }
                    }
                    for(var field in params){
                        if(tabPanel.items.get(i).getStore().proxy.extraParams&&tabPanel.items.get(i).getStore().proxy.extraParams.hasOwnProperty(field)&&field!='mediaType'&&field!='nodeid'){
                            params[field] += '#_#'+tabPanel.items.get(i).getStore().proxy.extraParams[field];
                        }
                    }

                    for(var field in tabPanel.items.get(i).getStore().proxy.extraParams){
                        if(!params.hasOwnProperty(field)){
                            params[field] = tabPanel.items.get(i).getStore().proxy.extraParams[field];
                        }
                    }

                    Ext.apply(tabPanel.items.get(i).getStore().proxy.extraParams, params);
                    params.nodeid = params.nodeid?params.nodeid:tabPanel.params.nodeid;
                    tabPanel.items.get(i).initGrid(params);
                }else{
                    tabPanel.items.get(i).initGrid({nodeid:params.nodeid?params.nodeid:tabPanel.params.nodeid});
                }
            }
        }
        tabPanel.params = params;
    },

    initSearchTabs:function(params){
        var tabPanel = this;
        for(var i=0;i<tabPanel.items.length;i++){
            if(tabPanel.items.get(i).xtype!='GridView') {
                var store = tabPanel.items.get(i).down('dataview').getStore();
                for (var field in params) {
                    store.proxy.extraParams[field] = params[field];
                    tabPanel.items.get(i).down('pagingtoolbar').setStore(store);
                    store.loadPage(1);
                }
            }
            if(tabPanel.items.get(i).xtype=='GridView'){
                tabPanel.items.get(i).initGrid(params);
            }
        }
    },

    initComponent: function() {
        var me = this;
        me.mediaListSort = me.up('MediaListSort');
        //alert(me.up('MediaListSort').url);
        if(me.hasSearchBar){
            searchBar(me);
        }

        this.callParent();
    },

    items: [{
        title: '缩略图显示',
        xtype:'dataviewMultisort',
        closable: false
    }]
});

/**
 * 图片列表
 */

Ext.define('Comps.view.MultiSort', {
    id: 'images-view',
    extend: 'Ext.panel.Panel',
    xtype: 'dataviewMultisort',
    //controller: 'dataview-multisort',
    frame: true,
    collapsible: true,
    mediaListSort:null,
    acrossSelections:[],        //跨页选择集合，用于保存跨页选择数据
    title: '声像按件采集',
    me:null,
    layout: 'fit',
    requires: [
        'Ext.data.*',
        'Ext.util.*',
        'Ext.toolbar.TextItem',
        'Ext.view.View',
        'Ext.ux.BoxReorderer',
        'Ext.ux.DataView.Animated'
    ],

    getMultiSort:function(me){
        var url = me.mediaListSort.photourl;
        return {
            autoLoad: true,
            sortOnLoad: true,
            fields: [
                {name: 'name', type: 'string', mapping: 'title'},
                {name: 'thumbid', type: 'string',mapping:'entryid'},
                {name: 'background', type: 'string',mapping:'background',convert:function (value,record) {
                    return encodeURIComponent(value);
                }},
                {name: 'type', type: 'string',mapping:'entrytype'}
            ],
            proxy: {
                type: 'ajax',
                url : url,
                reader: {
                    type: 'json',
                    rootProperty: 'content',
                    totalProperty: 'totalElements'
                }
            }
        };
    },

    initComponent: function() {
        var me = this;
         me.mediaListSort = me.up('MediaListSort');
        //alert(me.up('MediaListSort').url);
        if(me.mediaListSort.hasSearchBar){
            //me.initSearchBar();
        }
        if(me.mediaListSort.hasPageBar){
            me.initBbar();//初始化分页条
        }
        me.initTbar();//初始化按钮条
        me.initItem(me);//初始化列表
        this.callParent();
    },

    /**
     * 初始化搜索栏，包括在结果中搜索、搜索后标记搜索内容为红色
     */
    initSearchBar:searchBar,

    initTbar:tbar,

    initBbar:bbar,

    initItem:function(me){
        me.items = {
            xtype: 'dataview',
            itemId: 'dataview',
            reference: 'dataview',
            trackOver: true,
            overItemCls: 'x-item-over',
            draggable:false,
            plugins: [
                {
                    ptype: 'ux-animated-dataview'
                },
                Ext.create('Ext.ux.DataView.DragSelector', {}),
                Ext.create('Ext.ux.DataView.LabelEditor', {dataIndex: 'name'})
            ],

            prepareData: function (data) {
                Ext.apply(data, {
                    shortName: Ext.util.Format.ellipsis(data.title, 15),
                    sizeString: Ext.util.Format.fileSize(data.size),
                    dateString: Ext.util.Format.date(data.lastmod, "m/d/Y g:i a")
                });
                return data;
            },
            selectionModel: {
                mode: 'MULTI'
            },
            itemSelector: 'div.thumb-wrap',

            tpl: [
                '<tpl for=".">',
                '<div class="thumb-wrap" id="{name:stripTags}" onmouseover="imageOver(select{entryid})" onmouseout="imageout(select{entryid})">',
                '<center><div class="thumb"><img  src="/electronic/getThumbnailEntry?url={background}&entrytype={entrytype}" /></div>',
                '<div class="x-editable1" style="overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">{shortName:htmlEncode}</div></center>',
                //勾选按钮
                '<img id="select{entryid}" onclick="clickSelect(select{entryid})" src="/img/defaultMedia/uncheck.png" style="cursor: pointer;display: none; position: absolute;top: 3px;left:3px;width: 17px;height:17px"/>',
                '</div>',
                '</tpl>',
                '<div class="x-clear"></div>'
            ],
            listeners: {
                itemclick: function (view, record, it, index, e) {
                    var curItem = document.getElementById('select' + record.get('entryid'));
                    if(typeof e =='undefined'){  //跨页刷新选择选中
                        view.getSelectionModel().select(record, true);//加上true,不影响其他选择
                        return false;
                    }else if (typeof e.target.nodeName !== 'undefined' && e.target.nodeName.indexOf('IMG') !== -1 && typeof e.target.id !== 'undefined') {
                        if (e.target.id.indexOf('select') !== -1) {
                            if (curItem.src.indexOf('uncheck') !== -1) {
                                view.getSelectionModel().deselect(record);
                                Ext.Array.remove(me.acrossSelections, record);
                                //取消选择显示全选按钮
                                me.down('[itemId=selectAllId]').setText('全选');
                            } else {
                                view.getSelectionModel().select(record, true);//加上true,不影响其他选择
                                var store = view.getStore();
                                var count = 0;
                                Ext.Array.each(me.acrossSelections, function () {
                                    for (var i = 0; i < store.getCount(); i++) {
                                        var record = store.getAt(i);
                                        if (record.get('entryid') == this.get('entryid')) {
                                            count++;
                                            break;
                                        }
                                    }
                                });
                                //单个选择，如果已经选择了当前的所有，则显示取消全选按钮
                                if(count==store.getCount()){
                                    me.down('[itemId=selectAllId]').setText('取消全选');
                                }
                            }
                            return false;
                        }
                    }

                    // for (var i = 0; i < me.acrossSelections.length; i++) {
                    //     document.getElementById('select' + me.acrossSelections[i].get('entryid')).style.display = "none";
                    //     document.getElementById('select' + me.acrossSelections[i].get('entryid')).src = "/img/defaultMedia/uncheck.png";
                    // }
                    curItem.src = "/img/defaultMedia/checked.png";
                    curItem.style.display = "";
                    //单个选择显示全选按钮
                    me.down('[itemId=selectAllId]').setText('全选');
                },
                selectionchange: function (dv, items) {
                    if(items.length>0){
                        var pageSelect = [];
                        for (var i = 0; i < me.acrossSelections.length; i++) {
                            var obj = document.getElementById('select' + me.acrossSelections[i].get('entryid'));
                            //判断是否存在跨页选中
                            if(obj){
                                obj.src = "/img/defaultMedia/uncheck.png";
                                obj.style.display = "none";
                            }else{
                                pageSelect.push(me.acrossSelections[i]);
                            }
                        }
                        if(pageSelect.length > 0){
                            //存在跨页选中
                            me.acrossSelections = pageSelect;
                        } else{
                            //只是当前也的选择，不存在跨页选中
                            me.acrossSelections = [];
                        }
                        for (var i = 0; i < items.length; i++) {
                            var obj = document.getElementById('select' + items[i].get('entryid'));
                            if(obj){
                                obj.src = "/img/defaultMedia/checked.png";
                                obj .style.display = "";
                            }
                            //加入没有选中的
                            var flag = true;
                            for (var j = 0; j < me.acrossSelections.length; j++) {
                                if (me.acrossSelections[j].get('entryid') == items[i].get('entryid')) {
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag){
                                Ext.Array.push(me.acrossSelections, items[i]);
                            }
                        }
                        this.up('MediaListSort').selectedItem = me.acrossSelections;
                    }
                },

                //列表渲染完成后，给store添加load监听，数据加载后，选回已选的数据
                render:function (view) {
                    var store = view.getStore();
                    store.on('load', function (store, records, success, operation, eOpts) {
                        var count = 0;
                        setTimeout(function () {
                            Ext.Array.each(me.acrossSelections, function () {
                                for (var i = 0; i < records.length; i++) {
                                    var record = records[i];
                                    if (record.get('entryid') == this.get('entryid')) {
                                        view.fireEvent('itemclick',view,record);
                                        count++;
                                        break;
                                    }
                                }
                            });
                            //跨页刷新，如果当前已经选择了所有，则显示取消选择按钮，否则显示全选按钮
                            if(me.down('[itemId=selectAllId]')){
                                if(count==records.length){
                                    me.down('[itemId=selectAllId]').setText('取消全选');
                                }else{
                                    me.down('[itemId=selectAllId]').setText('全选');
                                }
                            }
                        }, 1000);
                    });
                }
            },
            store: me.getMultiSort(me)

        }
    }

});

Ext.define('Comps.view.GridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'GridView',
    itemId: 'GridView',
    dataUrl: '../js/json/listgrid.json',
    title: '当前节点：列表显示',
    hasSearchBar: false,
    mediaListSort:null,
    initTbar: tbar,
    initBbar: bbar,
    getMultiSort:function(me){
        var url = this.dataurl;
        return {
            autoLoad: true,
            sortOnLoad: true,
            fields: ['name', 'thumb', 'url', 'type'],
            proxy: {
                type: 'ajax',
                url : url,
                reader: {
                    type: 'json',
                    rootProperty: ''
                }
            }
        };
    },
    initComponent: function () {
        var me = this;
        me.mediaListSort = me.up('MediaListSort');
        me.dataUrl = me.mediaListSort.dataurl;
        me.initTbar();
        me.initBbar();
        this.callParent();
    }
});

function searchBar(me){
    me.tbar = [{
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
        style:{ 'background':'#FFF'},
        items:[{
            width: 100,
            xtype: 'label',
            text: '检索条件:',
            margin: '0 2 0 10'
        }, {
            width: 130,
            itemId: 'condition',
            store:{
                //autoLoad: true,
                proxy: {
                    type: 'ajax',
                    url:'/template/queryName',
                    extraParams:{nodeid:0},
                    reader: {
                        type: 'json',
                        rootProperty: 'content',
                        totalProperty: 'totalElements'
                    }
                }
            },
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
            width: 200,
            itemId: 'value',
            listeners:{
                search:function(field){
                    //获取检索框的值
                    var container = me.down('fieldcontainer');
                    var isCheckFind = me.down('[itemId=inresult]').getValue();
                    var condition = container.down('[itemId=condition]').getValue();
                    var operator = container.down('[itemId=operator]').getValue();
                    var content = field.getValue();
                    var tabPanel = me;
                    //检索数据
                    //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
                    var gridstore = me.down('dataview')?me.down('dataview').getStore():me.down('GridView');
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
                    var params = {
                        condition: searchcondition,
                        operator: searchoperator,
                        content: searchcontent
                    };
                    if(me.params&&me.params.id){
                        params.id = me.params.id;
                    }
                    params.nodeid = params.nodeid?params.nodeid:tabPanel.params.nodeid;
                    me.initTabs(params,isCheckFind);
                }
            }
        }, {
            xtype: 'checkbox',
            itemId:'inresult',
            boxLabel: '结果中检索'
        }, {
            xtype:'button',
            margin:'0 0 0 5',
            text:'<span style="color: #606060 !important;">取消选择</span>',
            style:{
                'background-color':'#f6f6f6 !important',
                'border-color':'#e4e4e4 !important'
            },
            handler:function(){
                for(var i = 0; i < me.getStore().getCount(); i++){
                    me.getSelectionModel().deselect(me.getStore().getAt(i));
                }
                me.acrossSelections = [];
            }
        },{
            xtype:'button',
            margin:'0 0 0 5',
            text:'<span style="color: #606060 !important;">列表显示</span>',
            style:{
                'background-color':'#f6f6f6 !important',
                'border-color':'#e4e4e4 !important'
            },
            handler:function(view){
                //console.log(view.up('MediaListSort'));
                var tabPanel = view.up('MediaListSort');
                for(var i=0;i<tabPanel.items.length;i++){
                    if(tabPanel.items.get(i).config.title.indexOf('列表显示')>-1){
                        tabPanel.setActiveTab(i);
                        return;
                    }
                }
                tab = tabPanel.insert(1,{
                    title: '列表显示 ',
                    xtype:'GridView'
                });

                tabPanel.setActiveTab(tab);
                tabPanel.initTabs(tabPanel.params);
            }
        },{
            xtype:'button',
            margin:'0 0 0 5',
            //iconCls:'x-search-close-icon',
            text:'<span style="color: #606060 !important;">关闭窗口</span>',
            style:{
                'background-color':'#f6f6f6 !important',
                'border-color':'#e4e4e4 !important'
            },
            handler:function(){
                parent.closeObj.close(parent.layer.getFrameIndex(window.name));
            }
        }
        ,{
            xtype: 'label',
            text: '已选择(0)个',
            itemId:'showMsg',
            margin: '0 2 0 10',
            style:'color:red;font-size:1.5em;',
            hidden:true
        }
        ]
    }]
}

function tbar(){
    var me = this;
    me.tbar =  me.mediaListSort.itembuttons;
}

function bbar(){
    var me = this;
    me.bbar = {
        xtype: 'pagingtoolbar',
        displayInfo: true,
        plugins: 'ux-progressbarpager',
        // store:me.getMultiSort(me),
        items:{ //添加分页选择下拉框
            xtype:'combo',
            store: new Ext.data.ArrayStore({
                fields: ['text', 'value'],
                data: [['2', 2], ['5', 5], ['10', 10], ['20', 20], ['50', 50], ['100', 100], ['300', 300]]
            }),
            value:20,//使用默认分页大小
            valueField: 'value',
            displayField: 'text',
            width: 80,
            editable: false,
            listeners:{
                change:function(field,newvalue,oldvalue){
                    me.down('[itemId=dataview]').getSelectionModel().deselectAll();
                    me.acrossSelections = [];
                    //选择分页大小，重新加载列表数据
                    var gridstore = me.down('dataview').getStore();
                    gridstore.setPageSize(newvalue);//设置列表store的大小
                    gridstore.loadPage(1);//重新加载第一页数据
                }
            }
        }
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

function imageOver(selectobj) {
    if (selectobj) {
        if(selectobj.length > 1) {
            if (typeof selectobj[0].style !== 'undefined') {//刷新后立即onmouseover,不知道为何selectobj是HTMLCollection，暂时规避
                selectobj[0].style.display = "";
            }
        } else{
            if (typeof selectobj.style !== 'undefined') {//刷新后立即onmouseover,不知道为何selectobj是HTMLCollection，暂时规避
                selectobj.style.display = "";
            }
        }
    }
}

function imageout(selectobj) {
    if(selectobj.length > 1) {
        if (selectobj[0].src && selectobj[0].src.indexOf('uncheck') !== -1) {
            selectobj[0].style.display = "none";
        }
    }
    else{
        if (selectobj.src && selectobj.src.indexOf('uncheck') !== -1) {
            selectobj.style.display = "none";
        }
    }
}
