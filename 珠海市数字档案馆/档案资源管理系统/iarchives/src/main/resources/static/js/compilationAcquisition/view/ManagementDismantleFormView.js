/**
 * Created by RonJiang on 2018/3/22 0022.
 */
Ext.define('CompilationAcquisition.view.ManagementDismantleFormView',{
    extend: 'Ext.form.Panel',
    xtype:'managementDismantleFormView',
    layout:'column',
    items:[{
        columnWidth:1,
        xtype:'combo',
        name:'dismantleType',
        itemId:'dismantleType',
        fieldLabel:'拆件方式',
        editable: false,
        //store:[['delete','直接删除'],['node','拆到其它节点']],
        store:[['delete','直接删除'],['node','拆到未归文件']],
        margin:'20 20 5 10',
        allowBlack:false,
        afterLabelTextTpl: [
            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
        ],
        listeners:{
            select:function(combo){
                var dismantleNode = this.findParentByType('form').getForm().findField('nodename');
                if('node' == combo.getValue()){
                    dismantleNode.enable();
                }else{
                    dismantleNode.disable();
                }
            }
        }
    },{
        columnWidth:1,
        xtype: 'managementTreeComboboxView',
        //fieldLabel: '拆件到分类',
        fieldLabel: '拆件到',
        editable: false,
        url: '/nodesetting/getWCLNodeByParentId',
        extraParams: {pcid:''},//根节点的ParentNodeID为空，故此处传入参数为空串
        allowBlank: false,
        name: 'nodename',
        itemId: 'dismantleNode',
        margin:'20 20 5 10',
        allowBlack:false,
        afterLabelTextTpl: [
            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
        ]
    },{
        columnWidth: 1,
        xtype: 'checkbox',
        itemId:'syncInnerFile',
        inputValue: 'syncInnerFile',
        margin:'20 20 5 10',
        name:'syncInnerFile',
        boxLabel:'同步处理对应的卷内记录'
    }],
    buttons:[{
        text: '保存',
        itemId: 'dismantleSave'
    }, '-',{
        text: '返回',
        itemId: 'dismantleBack'
    }]
});