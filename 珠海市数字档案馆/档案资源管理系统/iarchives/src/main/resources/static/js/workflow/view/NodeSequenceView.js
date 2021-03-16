/**
 * Created by luzc on 2020/6/16.
 */

Ext.define('Workflow.view.NodeSequenceView',{
    extend:'Ext.panel.Panel',
    xtype:'nodeSequenceView',
    itemId: 'nodeSequenceView',
    layout:'border',
    split:true,
    items:[{//显示当前节点条目视图
        region:'center',
        flex:4,
        xtype:'nodeSequenceGridView'//引入表单视图
    }],
    buttons:[{
        text:'上调',
        itemId:'up'
    },'-',{
        text:'下调',
        itemId:'down'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});
