/**
 * 分类设置视图
 */
Ext.define('Acquisition.view.dictionary.AcquisitionClassificationView',{
    extend:'Ext.panel.Panel',
	xtype:'acquisitionclassification',
	itemId: 'acquisitionclassificationid',
    layout:'card',
    activeItem:0,
    items:[{//分类设置第一步: 显示字典视图跟表单视图
    	itemId:'classificationFirstStep',
    	layout:'border',
        items:[{
        	region:'center',
	    	flex: 1.5,
	    	layout:'column',
	        itemId:'gridview',
	        xtype:'acquistionDictionaryView'//引入字典视图
	    },{
	    	region:'south',
	    	flex: 3.3,
	    	layout:'fit',
		    xtype:'acquisitionClassificationGridView'//引入表单视图
    	}]
    },{//分类设置第二部: 显示分类自动设置预览界面
    	itemId:'classificationSecondStep',
    	layout:'border',
    	split:true,
        items:[{
            region:'north',
            xtype:'fieldset',
            flex:1.5,
            margin:'auto',
            title: '说明',
            layout:'fit',
            items:[{
                xtype: 'label',
                style:'font-size:18px;color:red;line-height:50px',
                margin:'0',
                html:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;根据题名匹配分类字典进行自动分类。'
            }]
        },{//显示已选择的所有数据至预览界面
            region:'center',
            xtype:'entrygrid',
            flex:3.3,
            templateUrl:'/template/changeGrid',
            dataUrl:'/acquisition/entryIndexCaptures/',
            hasSearchBar:false
        }]
    }],
    buttons:[{
    	text: '分类设置',
    	itemId: 'classificationSet'
    },{
    	text: '分类自动设置',
    	itemId: 'classificationAutoSet'
    },{
    	text: '上一步',
    	itemId: 'previousStepBtn'
    },{
    	text: '设置',
    	itemId: 'setInfo'
    },{
        text: '返回',
        itemId: 'classificationBackBtn'
    }]
});