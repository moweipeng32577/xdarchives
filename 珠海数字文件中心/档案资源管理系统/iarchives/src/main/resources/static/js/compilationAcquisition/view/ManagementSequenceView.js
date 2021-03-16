/**
 * 数据采集卷内文件调序视图
 */
Ext.define('CompilationAcquisition.view.ManagementSequenceView',{
    extend:'Ext.panel.Panel',
	xtype:'managementSequenceView',
	itemId: 'managementSequenceView',
    layout:'border',
    split:true,
    items:[{
    	region:'north',
        xtype:'fieldset',
        flex:2,
        margin:'auto',
        title: '说明',
        layout:'fit',
        items:[{
            xtype: 'label',
            style:'font-size:18px;color:red;line-height:20px',
            margin:'0',
            html:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;顺序号、页号、页数可编辑。上调：所选记录顺序号-1,所选记录上一条记录的顺序号+1。'
        }]
    },{//显示当前节点条目视图
    	region:'center',
    	flex:4,
	    xtype:'managementSequenceGridView'//引入表单视图
    }],
    buttons:[{
        text:'上调',
        itemId:'up'
    },'-',{
        text:'下调',
        itemId:'down'
    },'-',{
        text:'保存',
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});