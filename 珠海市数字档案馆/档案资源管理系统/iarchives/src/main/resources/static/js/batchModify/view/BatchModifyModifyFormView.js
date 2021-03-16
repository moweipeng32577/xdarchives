/**
 * Created by RonJiang on 2018/1/25 0025.
 */
Ext.define('BatchModify.view.BatchModifyModifyFormView',{
    extend: 'Ext.form.Panel',
    layout:'border',
    xtype:'batchModifyModifyFormView',
    items:[{
        region:'north',
        xtype:'fieldset',
        height:80,
        margin:'5 120 5 20',
        title: '说明',
        layout:'fit',
        items:[{
            xtype: 'label',
            style:'font-size:17px;color:red',
            html:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;对多个字段值的内容，进行批量更新。&nbsp;&nbsp;&nbsp;&nbsp;温馨提示：修改档号组成字段的值后，需进行档号对齐，重新生成新档号。'
        }]
    },{
        region:'center',
        xtype:'fieldset',
        margin:'5 20 5 20',
        layout:'column',
        title: '字段列名',
        items:[{
            columnWidth: .3,
            xtype:'combobox',
            itemId:'templatefieldCombo',
            queryMode: 'local',
            name:'fieldname',
            editable:false,
            border:true,
            store:'BatchModifyTemplatefieldStore',
            displayField: 'fieldname',
            valueField:'fieldname'
        },{
            columnWidth: .6,
            name: 'fieldvalue',
            itemId:'updateFieldvalue',
            xtype: 'textfield',
            border:true
        },{
        	columnWidth: .6,
            xtype:'combobox',
            itemId:'enumfieldCombo',
            name:'code',
            editable: false,
            border: true,
            hidden: true,
            store:'BatchModifyTemplateEnumfieldStore',
            displayField: 'code',
            valueField:'value'
        },{
            columnWidth: 1,
            xtype: 'checkbox',
            itemId:'ifAllowEmpty',
            labelWidth:125,
            inputValue : true,
            margin:'5 0 5 0',
            fieldLabel:'替换值允许为空'
        },{
            columnWidth: .7,
            xtype:'fieldset',
            scrollable:true,
            height:270,
            items:[{
                xtype:'grid',
                style:'margin:0',
                selType: 'checkboxmodel',
                store:'FieldModifyPreviewGridStore',
                columns: [
                    {text: '字段编码',dataIndex: 'fieldcode',flex: 1,menuDisabled: true},
                    {text: '字段名称',dataIndex: 'fieldname',flex: 1,menuDisabled: true},
                    {text: '字段值',dataIndex: 'fieldvalue',flex: 2,menuDisabled: true}
                ]
            }]
        },{
            columnWidth: .15,
            margin:'10 0 0 10',
            layout:'column',
            items:[{
                columnWidth: 1,
                itemId:'addToModify',
                xtype:'button',
                margin:'0 0 5 0',
                text:'加入修改'
            },{
                columnWidth: 1,
                itemId:'deleteModify',
                xtype:'button',
                margin:'5 0 5 0',
                text:'删除修改'
            },{
                columnWidth: 1,
                itemId:'clear',
                xtype:'button',
                margin:'5 0 0 0',
                text:'清除'
            }]
        }]
    }],
    buttons:[{
        text:'获取预览',
        itemId:'getPreview'
    },{
        text:'退出',
        itemId:'exit'
    }]
});