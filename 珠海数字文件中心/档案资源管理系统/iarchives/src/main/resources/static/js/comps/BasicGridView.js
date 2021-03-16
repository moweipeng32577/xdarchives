/**
 * 基础表格组件。
 * Comps.view.BasicGridView
 * 构造基本数据组件，支持以下功能：
 * 1.跨页选择；
 * 2.分页栏，包括分页大小选择；功能开关hasPageBar
 * 3.行序号；功能开关hasRownumber
 * 4.整合拖拽插件，可用于快速排序，需监听drop(node,data,overModel,dropPosition,eOpts)事件调用处理方法；功能开关allowDrag
 * 5.整合检索栏；功能开关hasSearchBar
 *
 * 注：
 * 1.跨页选择是通过record的id去判断，所以record中必须有id属性。
 * 如果没有id是userid，organid这种主键，需在model中添加映射，如：{name: 'id', type: 'string', mapping: 'organid'}
 * 2.设置开启检索栏，必须设置检索字段searchstore:Ext.data.Store / Array。
 * 如果是Array，格式为[{item: "organname", name: "机构名称"}, {item: "servicesname", name: "服务名称"}]
 * item为valuefield,name为displayfield
 * 如果是Store，需返回List<ExtSearchData>
 *
 * V1.0
 * 初始版本
 * Created by Rong on 2017/12/14.
 * V1.1
 * 1)修复checkboxmodel和拖拽插件同时使用的BUG
 * 2)修复第一次检索就勾选结果中检索的BUG
 * 3)修改检索结果标红的实现方式，不影响数据实际值
 * 4)添加取消选择功能
 * 5)添加关闭功能
 * Modify by Rong on 2017/12/19.
 * V1.2
 * 1)将跨页选择清除放到initGrid中操作
 * 2)对外提供接口增加delReload:function(delcount)，用于删除数据后刷新（解决最后一页删除问题）
 * Modify by Rong on 2017/12/22.
 * V1.3
 * 1)修复数据查询更新后跨页选择项的Bug
 * Modify by Rong on 2018/03/22
 * V1.4
 * 调整标题栏样式（减小标题栏高度，增加内容显示空间）
 * Modify by Rong on 2018/09/05
 * v1.5
 * 1)修改delReload 方法为post请求方式
 * 2)调整initSelModel 增加自动勾选判断
 * Modify by sunk on 2019/04/20
 * v1.6
 * 1)修改选中条目与实际拿到的条目不对应问题
 * 2) 修复v1.5 2)自动勾选是分页查询sql的问题
 * Modify by sunk on 2019/04/23
 * v1.7
 * 1)调整初始化搜索栏 initSearchBar 增加即时搜索
 */
Ext.define('Comps.view.BasicGridView',{
    extend:'Ext.grid.Panel',    //继承gridpanel
    xtype:'basicgrid',

    rowLines:true,               //开启行线条
    columnLines:true,           //开启列线条

    //配置项开关start
    hasPageBar:true,            //分页栏
    hasSearchBar:true,          //搜索栏
    hasRownumber:true,          //行序号
    hasCheckColumn:true,        //选择框
    hasCloseButton:true,        //关闭按钮
    hasCancelButton:true,       //取消选择按钮
    hasSelectAllBox:false,      //选择所有页
    hasToMediaButton:true,        //切换到声像缩列图按钮
    allowDrag:false,            //可拖拽
    //配置项开关end

    //检索字段数据源
    searchstore:null,

    //数据来源，用于填充基础查询条件
    dataParams:{},

    actions:{
        file:{
            iconCls:'x-action-electronic-icon',
            tooltip:'电子文件',
            getClass : function (v, metadata, r, rowIndex, colIndex, store) {
                if(typeof(v) == "undefined"){
                    return "x-hidden";
                }else{
                    return "x-action-electronic-icon";
                }
            },
            handler:function(view,row){
                var grid = view.grid;
                var eleview = grid.fireEvent('eleview',{
                    grid:grid,
                    entryid:grid.getStore().getAt(row).get('entryid'),
                    selectedRow:grid.getStore().getAt(row)
                });
                //打开电子原文查看界面时，隐藏底部保存及连续录入按钮
                if(grid.parentXtype){
                    var form = grid.up(grid.parentXtype).down(grid.formXtype);
                    changeBtnStatus(form);
                }
            }
        }
    },

    /**
     * 渲染控件
     */
    initComponent:function(){
        this.acrossSelections = new Array();        //跨页选择集合，用于保存跨页选择数据
        this.acrossDeSelections  =  new Array();        //跨页不选择集合，用于保存跨页不选择数据
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
        //构造拖拽插件
        if(me.allowDrag){ me.initDrag(); }
        //填加行序号
        if(me.hasRownumber){
            var rownumberExist;
            for(var i=0;i<me.columns.length;i++){
                if(me.columns[i].xtype=='rownumberer'){
                    rownumberExist = true;
                    break;
                }
            }
            if(!rownumberExist){
                me.columns = Ext.Array.insert(me.columns, 0, [{xtype: 'rownumberer', align: 'center', width:40}]);
            }
        }
        //构造列表选择器
        if(me.hasCheckColumn){
            me.initSelModel();
        }

		//设置标题栏样式
        if(me.title && me.title != ''){
            me.header = {
                height : 35,
                padding : '5 5 5 10',
                title : me.title
            }
        }

        me.callParent();
    },

	/**
     * 重写grid的setTitle方法，修改了header样式，title需要通过header设置
     */
    setTitle:function(src){
        if(!src || src == ''){
            return;
        }
        var me = this;
        if(me.header && !me.header.height){
            me.header.setConfig('height',35);
            me.header.setConfig('padding', 5);
            me.header.setTitle(src);
        }else if(me.header && me.header.setTitle){
            me.header.setTitle(src);
        }else{
            me.header = {
                height : 35,
                padding : '5 5 5 10',
                title : src
            }
        }
    },

    /**
     * 初始化列表的选择框，实现跨页选择功能
     */
    initSelModel:function(){
        var me = this;
        me.selModel = {
            selType:'checkboxmodel',
            listeners:{
                select: function (modal, record, index) {
                    if (me.getStore().selectall) {
                        //选择数据时，将record从acrossDeSelections集合中移出
                        for (var i = 0; i < me.acrossDeSelections.length; i++) {
                            if (me.acrossDeSelections[i].get('id') == record.get('id')) {
                                me.acrossDeSelections.splice(i, 1);
                                break;
                            }
                        }
                    } else {
                        if(index<0){//2019年4月20日09:34:11  勾选后翻页会出现的自动勾选 （index的传值是负数）
                            for (var i = 0; i < me.acrossDeSelections.length; i++) {
                                if (me.acrossSelections[i].get('id') == record.get('id')) {
                                    return;
                                }
                            }
                            Ext.Array.push(me.acrossSelections, record);
                        }else{
                        //选择数据时，如果集合中没有，则将record放入集合
                            for (var i = 0; i < me.acrossSelections.length; i++) {
                                if (me.acrossSelections[i].get('id') == record.get('id')) {
                                    return;
                                }
                            }
                            Ext.Array.push(me.acrossSelections, record);
                        }
                    }
                },
                deselect: function (modal, record, index) {
                    if (me.getStore().selectall) {
                        for (var i = 0; i < me.acrossDeSelections.length; i++) {
                            if (me.acrossDeSelections[i].get('id') == record.get('id')) {
                                return;
                            }
                        }
                        Ext.Array.push(me.acrossDeSelections, record);
                    } else {
                        //取消选择数据时，将record从集合中移出
                        for (var i = 0; i < me.acrossSelections.length; i++) {
                            if (me.acrossSelections[i].get('id') == record.get('id')) {
                                me.acrossSelections.splice(i, 1);
                                break;
                            }
                        }
                    }
                }
            },
            //重写事件触发方法。解决同时存在拖拽和checkbox时，多选后无法选中checkbox的BUG
            processColumnEvent:function(type, view, cell, recordIndex, cellIndex, e, record, row){
                if(me.allowDrag && e.type == 'click'){
                    me.focus();
                    return;
                }
                var navModel = view.getNavigationModel();
                if ((e.type === 'keydown' && view.actionableMode && e.getKey() === e.SPACE) ||
                    (!this.checkOnly && e.type === this.triggerEvent)) {
                    navModel.fireEvent('navigate', {
                        view: view,
                        navigationModel: navModel,
                        keyEvent: e,
                        position: e.position,
                        recordIndex: recordIndex,
                        record: record,
                        item: e.item,
                        cell: e.position.cellElement,
                        columnIndex: e.position.colIdx,
                        column: e.position.column
                    });
                }
            },
            //重写判断选择项方法，返回是否跨页选择
            hasSelection:function(){
                if (me.getStore().selectall) {
                    return me.acrossDeSelections.length < me.getStore().totalCount;
                }
                return me.acrossSelections.length > 0;
            },
            //重写获取选择项方法，返回跨页选择结果
            getSelection:function(){
                return me.acrossSelections;
            },
            //重写清除选择项方法
            clearSelections: function() {
                me.acrossSelections = [];
                me.acrossDeSelections = [];
                var selected = this.getSelected();
                if (selected) {
                    selected.clear();
                }
                this.lastSelected = null;
            },
            //获取选择项的数量
            getSelectionLength:function(){
                if (me.getStore().selectall) {
                    return me.getStore().totalCount - me.acrossDeSelections.length;
                }
                return me.acrossSelections.length;
            }
        };
        //列表渲染完成后，给store添加load监听，数据加载后，选回已选的数据
        me.on('render',function(store,records,success){
            store.on('beforeload',function(store){
                if(store.paging == undefined || !store.paging){
                    me.getSelectionModel().clearSelections();
                }
            });
            store.on('load',function(store,records,success,operation,eOpts){
                if(store.selectall){
                    me.getSelectionModel().selectAll(true);
                    if (store.paging) {//不检索时
                        var selModel = me.getSelectionModel();
                        Ext.Array.each(me.acrossDeSelections, function () {
                            for (var i = 0; i < records.length; i++) {
                                var record = records[i];
                                if (record.get('id') == this.get('id')) {
                                    Ext.apply(this, record);
                                    selModel.deselect(record, false);
                                }
                            }
                        });
                        store.paging = false;
                    }
                }else{
                    if (store.paging) {
                        var selModel = me.getSelectionModel();
                        Ext.Array.each(me.acrossSelections, function () {
                            for (var i = 0; i < records.length; i++) {
                                var record = records[i];
                                if (record.get('id') == this.get('id')) {
                                    Ext.apply(this, record);
                                    selModel.select(record, true, false);    //选中record，并且保持现有的选择，不触发选中事件
                                }
                            }
                        });
                        store.paging = false;
                    }
                }
            });
        });
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
            default:{
                height:25
            },
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
                        var gridstore = me.getStore();
                        gridstore.setPageSize(newvalue);//设置列表store的大小
                        gridstore.loadPage(1);//重新加载第一页数据
                    }
                }
            },
            listeners:{
                beforechange:function(el, page, eopts){
                    var gridstore = me.getStore();
                    gridstore.paging = true;
                },
                afterlayout:function(el){
                    me.down('[itemId=afterTextItem]').setStyle('top','6px');
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
                    // change:function(field){
                    //      var instantSearch = me.down('[itemId=instantSearch]').getValue();
                    //      if(instantSearch){
                    //         //getSearch(field,me);
                    //          setTimeout(function(){
                    //              getSearch(field,me);
                    //          }, 600);
                    //      }
                    //  },
                    search:function(field){
                        getSearch(field,me);
                    }
                }
            },{
            //     xtype: 'checkbox',
            //     itemId:'instantSearch',
            //     boxLabel: '即时搜索'
            // },{
                xtype: 'checkbox',
                itemId:'inresult',
                boxLabel: '结果中检索'
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
                for(var i = 0; i < me.getStore().getCount(); i++){
                    me.getSelectionModel().deselect(me.getStore().getAt(i));
                }
                me.acrossSelections = [];
                me.acrossDeSelections = [];
                me.getStore().selectall = false;
                if (me.down('[itemId=selectAll]') != null) {
                    me.down('[itemId=selectAll]').setValue(false);
                }
            }
        };
        var close = {
            xtype: 'button',
            itemId: 'basicgridCloseBtn',
            margin: '0 0 0 5',
            //iconCls:'x-search-close-icon',
            text: '<span style="color: #000000 !important;">关闭</span>',
            tooltip: '关闭当前页面',
            style: {
                'background-color': '#f6f6f6 !important',
                'border-color': '#e4e4e4 !important'
            },
            handler: function (btn) {
                parent.closeObj.close(parent.layer.getFrameIndex(window.name));
            }
        };
        var toMedia = {//切换到声像缩略图
            xtype: 'button',
            itemId: 'toMediaBtn',
            margin: '0 0 0 5',
            hidden: true,
            //iconCls:'x-search-close-icon',
            text: '<span style="color: #000000 !important;">缩列图显示</span>',
            tooltip: '切换到声像缩略图页面',
            style: {
                'background-color': '#f6f6f6 !important',
                'border-color': '#e4e4e4 !important'
            }
        };
        var selectAll = {
            xtype: 'checkbox',
            itemId:'selectAll',
            boxLabel: '选择所有页',
            listeners: {
                change: function (field) {
                    if (field.checked) {
                        me.getSelectionModel().selectAll(false);
                        me.acrossSelections = [];
                        me.acrossDeSelections = [];
                        me.getStore().selectall = true;
                    } else {
                        for (var i = 0; i < me.getStore().getCount(); i++) {
                            me.getSelectionModel().deselect(me.getStore().getAt(i));
                        }
                        me.acrossSelections = [];
                        me.acrossDeSelections = [];
                        me.getStore().selectall = false;
                    }
                }
            }
        };
        if(me.hasSelectAllBox){
            item[0].items.push(selectAll);
        }
        if(me.hasCancelButton){
            item[0].items.push(cancelSelected);
        }
        if(me.hasCloseButton){
            item[0].items.push(close);
        }
        if(me.hasToMediaButton){
            item[0].items.push(toMedia);
        }
        me.dockedItems = item;
    },

    /**
     * 初始化列表拖拽插件
     */
    initDrag:function(){
        var me = this;
        me.viewConfig = {
            plugins : { ptype: 'gridviewdragdrop',containerScroll: true }
        };
    },

    //--------------------------------------------提供外部使用函数---------------------------------------------------
    /**
     * 初始化列表数据
     * @param dataParams 列表基础过滤条件，不受搜索栏影响的内容。如{organid:'0'}
     * @returns {basicgrid} 当前列表
     */
    initGrid:function(dataParams){
        var me = this;

        //切换数据时，清除掉因搜索添加的列renderer,并清除跨页选择项
        Ext.Array.each(me.getColumns(), function(){
            var column = this;
            if(column.renderer != false && column.renderer.isSearchRender){
                column.renderer = false;
            }
        });
        me.getSelectionModel().clearSelections();

        if(me.dataUrl == ''){
            throw new Error('grid未设置数据源dataUrl!');
        }
        //初始化列表数据
        if(typeof(dataParams) != 'undefined'){
            me.dataParams = dataParams
        }

        var store = me.getStore();
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
        if (me.down('[itemId=selectAll]') != null) {
            me.down('[itemId=selectAll]').setValue(false);
        }
        return me;
    },
    /**
     * 不初始化页数的刷新
     * @param dataParams
     */
    notResetInitGrid:function (dataParams) {
        var me = this;
        //切换数据时，清除掉因搜索添加的列renderer,并清除跨页选择项
        // Ext.Array.each(me.getColumns(), function(){
        //     var column = this;
        //     if(column.renderer != false && column.renderer.isSearchRender){
        //         column.renderer = false;
        //     }
        // });
        me.getSelectionModel().clearSelections();
        var store = me.getStore();
        //初始化列表数据
        if(typeof(dataParams) != 'undefined'){
            me.dataParams = dataParams
        }
        Ext.apply(store.getProxy(),{
            extraParams:me.dataParams
        });
        store.reload();
        me.fireEvent('render',store);
        if (me.down('[itemId=selectAll]') != null) {
            me.down('[itemId=selectAll]').setValue(false);
        }
    },
    /**
     * 删除数据后刷新列表数据。
     * 处理删除最后一页数据，回读上一页的问题
     * @param delcount 删除的数据量
     * @param fn 回掉函数，数据刷新成功后执行
     */
    delReload:function(delcount, fn){
        var me = this;
        me.getSelectionModel().clearSelections();

        var store = me.getStore();
        var loadpage = store.currentPage;
        if(delcount > store.getCount()){
            //跨页多选删除，计算删除的页数
            var totalPageCount = Math.ceil(store.getTotalCount() / store.pageSize);
            var delPageCount = Math.ceil(delcount / store.pageSize);
            loadpage = 1;
            if(delPageCount < totalPageCount){
                loadpage = totalPageCount - delPageCount;
            }
        }else if(delcount == store.getCount()){
            //删除当前页所有数据
            //如果不是最后一页，则刷新当前页
            //如果是最后一页且不是第一页，则读取上一页数据
            var totalPageCount = Math.ceil(store.getTotalCount() / store.pageSize);
            if(totalPageCount == store.currentPage && store.currentPage > 1){
                loadpage = store.currentPage - 1;
            }
        }
        store.getProxy().actionMethods={read:'POST'};
        store.loadPage(loadpage, {callback:fn});
        if (me.down('[itemId=selectAll]') != null) {
            me.down('[itemId=selectAll]').setValue(false);
        }
    }
});

function changeBtnStatus(form){
    var savebtn = form.down('[itemId=save]');
    var continuesave = form.down('[itemId=continuesave]');
    var tbseparator = form.getDockedItems('toolbar')[0].query('tbseparator');
    if(savebtn){
        savebtn.setVisible(false);
    }
    if(continuesave){
        continuesave.setVisible(false);
    }
    if(tbseparator.length==1){
        tbseparator[0].setVisible(false);
    }
    if(tbseparator.length==2){
        tbseparator[0].setVisible(false);
        tbseparator[1].setVisible(false);
    }
}

//搜索栏
function getSearch(field,me) {
    var container = me.down('fieldcontainer');
    var condition = container.down('[itemId=condition]').getValue();
    var operator = container.down('[itemId=operator]').getValue();
    var content = field.getValue();
    //检索数据
    //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
    var gridstore = me.getStore();
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

    //检索数据前,修改column的renderer，将检索的内容进行标红
    Ext.Array.each(me.getColumns(), function(item){
        var column = item;
        if(!inresult && column.xtype == 'gridcolumn' && column.dataIndex!='authenticity'
            && column.dataIndex!='integrity' && column.dataIndex!='usability'
            && column.dataIndex!='safety'){
            column.renderer = function(value){
                return value;
            }
        }
        if(column.dataIndex=="tdn"){
            column.renderer = function(value) {
                return value['nodename'];
            }
        }
        if(column.dataIndex == condition){
            var searchstrs = [];
            var conditions = searchcondition.split(XD.splitChar);
            var contents = searchcontent.split(XD.splitChar);
            for(var i =0;i<conditions.length;i++){
                if(conditions[i] == condition){
                    searchstrs.push(contents[i]);
                }
            }
            var char = ["\\", "^", "$", "+", "?", "=", "!", ".", "(", ")", "/", "[", "]", "{", "}"];//需要转义的符号
            column.renderer = function (v) {
                var newSearchstrs = searchstrs.slice();//复制原数组
                for (var i = 0; i < newSearchstrs.length; i++) {
                    for (var j = 0; j < char.length; j++) {
                        newSearchstrs[i] = newSearchstrs[i].replace(eval("/\\" + char[j] + "/g"), "\\" + char[j]);//或new RegExp(key,'g')
                    }
                }
                if (typeof(me.searchColumnRenderer) == "undefined") {
                    var reTag = /<(?:.|\s)*?>/g;
                    var value = v.replace(reTag, "");
                    var reg = new RegExp(newSearchstrs.join('|'), 'g');
                    return value.replace(reg, function (match) {
                        return '<span style="color:red">' + match + '</span>'
                    });
                } else {
                    return me.searchColumnRenderer(v, newSearchstrs);
                }
            }
            column.renderer.isSearchRender = true;
        }
    });
    gridstore.loadPage(1);
}