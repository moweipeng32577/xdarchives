/**
 * Created by Administrator on 2018/9/12.
 */

Ext.define('User.view.UserSequenceView',{
    extend:'Ext.panel.Panel',
    xtype:'userSequenceView',
    itemId: 'userSequenceView',
    layout:'border',
    split:true,
    items:[{//显示当前节点条目视图
        region:'center',
        flex:4,
        xtype:'userSequenceGridView'//引入表单视图
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

