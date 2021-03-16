/**
 * Created by RonJiang on 2017/11/29 0029.
 */
Ext.define('CompilationAcquisition.view.ManagementFilingView',{
    extend: 'Ext.panel.Panel',
    xtype:'managementfiling',
    layout:'card',
    activeItem:0,
    
    items:[{//归档第一步：表单窗口
        laout:'vbox',
        xtype:'form',
        itemId:'filingFirstStep',
        items: [{
            xtype: 'managementTreeComboboxView',
            fieldLabel: '档案分类',
            width:'80%',
            editable: false,
            url: '/nodesetting/getYGDNodeByParentId',
            extraParams: {pcid:''},//根节点的ParentNodeID为空，故此处传入参数为空串
            allowBlank: false,
            name: 'nodename',
            itemId: 'nodenameitemid',
            margin:'40 20 5 50'
        },{
            xtype:'fieldset',
            height:220,
            margin:'25 120 5 50',
            title: '说明',
            layout:'fit',
            items:[{
                xtype: 'label',
                style:'font-size:18px;color:red;line-height:50px',
                margin:'20 0 0 0',
                html:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;选取需要归档的档案分类，点击“下一步”进入归档预览界面，点击“返回”关闭归档窗口。若选择分类节点的模板或档号设置不正确，将无法进行下一步归档操作，请在“系统设置”-“模板维护”中设置该节点的模板及档号。'
            }]
        }]
    },{//归档第二步：列表窗口
        itemId:'filingSecondStep',
        layout:'border',
        split:true,
        items:[{
            region:'north',
            xtype :'dynamicfilingform' ,
            flex:1,
            itemId:'dynamicfilingform',
            calurl:'/management/getCalValue'
        },{
            region:'center',
            xtype:'entrygrid',
            flex:2,
            templateUrl:'/template/changeGrid',
            dataUrl:'/management/entryIndexes/',
            hasSearchBar:false,
            tbar:[{
                text:'保管期限调整',
                itemId:'retentionAdjust'
            }]
        }]
    }],
    buttons:[{
        text: '下一步',
        itemId: 'filingNextStepBtn'
    }, '-',{
        text: '返回',
        itemId: 'filingBackBtn'
    }, '-',{
        text: '归档',
        itemId:'filingBtn'
    }, '-',{
        text: '上一步',
        itemId:'filingpreviousStepBtn'
    }]
});