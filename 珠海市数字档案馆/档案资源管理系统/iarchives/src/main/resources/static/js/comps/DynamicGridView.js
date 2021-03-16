/**
 * Created by Rong on 2017/10/25.
 *
 * 动态数据表格组件。
 * Comps.view.DynamicGridView
 * 根据后台模板配置，动态构建数据表格表头
 *
 * 关键参数：nodeid，数据节点ID。模板配置通过此参数加载
 *
 */
Ext.define('Comps.view.DynamicGridView',{
    extend:'Ext.grid.Panel',
    xtype:'dynamicgrid',
    nodeid:'',
    selType: 'checkboxmodel',
    store:[],
    columns: [],
    tbar: [],
    bbar: {
        xtype: 'pagingtoolbar',
        displayInfo: true,
        plugins: 'ux-progressbarpager'
    },
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
                grid.fireEvent('eleview',{
                    grid:grid,
                    entryid:grid.getStore().getAt(row).get('entryid')
                });
            }
        }
    },
    listeners:{
        // render:function(grid){
        //     this.initGrid(grid);
        // }
    },

    initGrid:function(){
        this.initColumns();
        this.initDatas();
    },

    //初始化数据表格列设置
    initColumns:function(){
        //请求后台数据表格配置，动态加载
        Ext.Ajax.request({
            scope:this,
            url: '/template/grid',
            params:{
                nodeid:this.nodeid
            },
            success: function(response, opts) {
                var columns = [{ xtype: 'rownumberer',align: 'center' }];       //第一列为序号

                columns.push({
                    xtype:'actioncolumn',
                    resizable:false,//不可拉伸
                    hideable:false,//menu中不显示
                    header:'原文',
                    dataIndex:'eleid',
                    width:60,
                    align:'center',
                    items:['@file']
                })

                //解析模板配置，依次将模板放入到列中
                var obj = Ext.decode(response.responseText);
                for(var i = 0; i < obj.length; i++){
                    columns.push({
                        header:obj[i].fieldname,        //列头描述
                        dataIndex:obj[i].fieldcode,     //列对应字段
                        width:obj[i].gwidth,            //列宽
                        hidden:obj[i].ghidden           //列是否隐藏显示
                    });
                }

                //重新配置数据表格的列
                this.reconfigure(columns);
            }
        });
    },

    //初始化数据表格数据源
    initDatas:function(){
        var gridstore = Ext.create('Ext.data.Store', {
            pageSize:XD.pageSize,                                    //每页显示数据量
            proxy: {
                type: 'ajax',
                extraParams:{
                    nodeid:this.nodeid
                },
                url: this.dataurl,
                reader: {
                    type: 'json',
                    rootProperty:'content',             //数据根目录
                    totalProperty:'totalElements'      //数据总数
                }
            },
            autoLoad: true
        });
        this.reconfigure(gridstore);
    },

    //初始化列表功能按钮组
    initTbar:function(){
        var topbar = this.getDockedItems('toolbar[dock="top"]')[0];
        //预留此功能，暂未实现
    }
});