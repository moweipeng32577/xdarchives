/**
 *
 * 动态数据表格组件，用于业务数据显示。
 * Comps.view.EntryGridView
 * 主要功能包括：
 * 1.列表前添加电子文件列，对应字段eleid，有值（即条目有对应电子文件）则显示图标，空值不现实图标
 * 点击图标时，触发事件eleview(grid,entryid)
 * 2.读取后台模板设置，动态设置列表表头描述、列宽、对应数据字段、是否隐藏
 * 3.根据传入的参数dataUrl，动态读取后台数据
 *
 * V1.0
 * 初始版本
 * Created by Rong on 2017/10/25.
 * V1.1
 * 修改为继承BasicGridView
 * Created by Rong on 2017/12/22.
 */
Ext.define('Template.view.EntryGridView',{
    extend:'Template.view.BasicGridView',
    xtype:'entrygrid',
    columns:[],
    isOpenEle:true,//是否打开原文字段
    initGrid:function(dataParams,type){
        var me = this;
        if(typeof(dataParams) != 'undefined'){
            me.dataParams = dataParams
        }

        me.initColumns();
        if(type){
            me.initDatasNew();
        }else{
            me.initDatas();
        }
        me.callParent([me.dataParams]);
    },

    //初始化数据表格列设置
    initColumns:function(){
        var me = this;
        var templateUrl;
        if (typeof(me.templateUrl) != 'undefined') {
            templateUrl = me.templateUrl;
        } else {
            templateUrl = '/template/grid'
        }
        //请求后台数据表格配置，动态加载
        Ext.Ajax.request({
            url: templateUrl,
            params:me.dataParams,
            success: function(response, opts) {
                var columns = [{ xtype: 'rownumberer',width:40,align: 'center' }];       //第一列为序号
                if (me.isOpenEle) {
                    columns.push({
                        xtype:'actioncolumn',
                        resizable:false,//不可拉伸
                        hideable:false,
                        header:'原文',
                        dataIndex:'eleid',
                        sortable:true,
                        width:60,
                        align:'center',
                        items:['@file'],
                        listeners:{
                            headerclick: function (ct, c, e) {
                                var store = c.up('grid').getStore();
                                if(c.dataIndex!=='archivecode'&&store.totalCount > 100000){
                                    /*e.stopPropagation();*/
                                    XD.msg('查询数据量大于10万,只支持本页面排序');
                                    store.setRemoteSort(false);
                                }else{
                                    store.setRemoteSort(true);
                                }
                            }
                        }
                    });
                }
                //解析模板配置，依次将模板放入到列中
                var obj = Ext.decode(response.responseText);
                for(var i = 0; i < obj.length; i++){
                    columns.push({
                        header:obj[i].fieldname,        //列头描述
                        dataIndex:obj[i].fieldcode,   //列对应字段
                        width:obj[i].gwidth,            //列宽
                        hidden:obj[i].ghidden,           //列是否隐藏显示
                        listeners:{
                            headerclick: function (ct, c, e) {
                                var store = c.up('grid').getStore();
                                if(c.dataIndex!=='archivecode'&&store.totalCount > 100000){
                                    /*e.stopPropagation();*/
                                    XD.msg('查询数据量大于10万,只支持本页面排序');
                                    store.setRemoteSort(false);
                                }else{
                                    store.setRemoteSort(true);
                                }
                            }
                        }
                    });
                }

                if(me.addNodenameColumn==true){//额外增加“分类”和“数据节点全名”两列
                    columns.push({
                        xtype:'gridcolumn',
                        header:'分类',
                        dataIndex:'tdn',
                        width:150,
                        renderer:function(value){
                            return value['nodename'];
                        }
                    },{
                        xtype:'gridcolumn',
                        header:'数据节点全名',
                        dataIndex:'nodefullname',
                        width:700
                    });
                }

                //重新配置数据表格的列
                me.reconfigure(null,columns);
            }
        });
    },

    //初始化数据表格数据源
    initDatas:function(){
        var me = this;
        var gridstore = Ext.create('Ext.data.Store', {
            model:'EntryModel',
            remoteSort:true,
            pageSize:XD.pageSize,                        //每页显示数据量
            proxy: {
                type: 'ajax',
                url: me.dataUrl,
                timeout:XD.timeout,
                reader: {
                    type: 'json',
                    rootProperty:'content',             //数据根目录
                    totalProperty:'totalElements'      //数据总数
                }
            }
        });
        me.reconfigure(gridstore);
        //me.fireEvent('render',gridstore);
    },
    //为解决请求头过大而将请求方式改为post
    initDatasNew:function(){
        var me = this;
        var gridstore = Ext.create('Ext.data.Store', {
            model:'EntryModel',
            remoteSort:true,
            pageSize:XD.pageSize,                        //每页显示数据量
            proxy: {
                type: 'ajax',
                url: me.dataUrl,
                timeout:XD.timeout,
                actionMethods:{
                    create:"post",
                    read:"post",
                    update:"post",
                    destroy:"post"
                },
                reader: {
                    type: 'json',
                    rootProperty:'content',             //数据根目录
                    totalProperty:'totalElements'      //数据总数
                }
            }
        });
        me.reconfigure(gridstore);
    }
});

Ext.define('EntryModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',  type: 'string', mapping:'entryid'}
    ]
});