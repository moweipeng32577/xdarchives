/**
 * Created by RonJiang on 2018/2/3 0003.
 */
Ext.define('BatchModify.view.BatchModifyReplaceFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'batchModifyReplaceFormView',
    layout:'border',
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
            html:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;把一个字段值中的字符，替换成另一个字符'
        }]
    },{
        region:'center',
        xtype:'fieldset',
        margin:'5 160 5 20',
        layout:'column',
        title: '内容设置',
        items:[{
            columnWidth: .7,
            xtype: 'combobox',
            itemId:'templatefieldCombo',
            queryMode: 'local',
            name:'fieldname',
            labelWidth:100,
            editable:false,
            border:true,
            store:'BatchModifyTemplatefieldStore',
            displayField: 'fieldname',
            valueField:'fieldname',
            fieldLabel:'修改字段',
            allowBlank:false
        },{
            columnWidth:0.02,
            xtype:'displayfield',
            value:'<label style="color:#ff0b23;!important;">*</label>'
        },{
            columnWidth: .7,
            xtype: 'textfield',
            itemId:'searchContentText',
            name:'searchcontent',
            labelWidth:100,
            inputValue : true,
            margin:'5 0 5 0',
            fieldLabel:'查找内容',
            allowBlank:false
        },{
            columnWidth:0.02,
            xtype:'displayfield',
            value:'<label style="color:#ff0b23;!important;">*</label>'
        },{
            columnWidth: .7,
            xtype: 'textfield',
            itemId:'replaceContentText',
            name:'replacecontent',
            labelWidth:100,
            inputValue : true,
            margin:'5 0 5 0',
            fieldLabel:'替换内容'
        },{
            columnWidth: 1,
            xtype: 'checkbox',
            itemId:'ifContainSpaces',
            labelWidth:125,
            inputValue : true,
            margin:'5 0 5 0',
            fieldLabel:'包含前后空格'
        },{
            columnWidth: 1,
            xtype: 'checkbox',
            itemId:'ifAllowEmpty',
            labelWidth:125,
            inputValue : true,
            margin:'5 0 5 0',
            fieldLabel:'替换值允许为空'
        }]
    }],
    buttons:[{
        xtype: "label",
        itemId:'tips',
        style:{color:'red'},
        text:'温馨提示：红色外框表示输入非法数据！',
        margin:'6 2 5 4'
    },{
        text:'获取预览',
        itemId:'getPreview'
    },{
        text:'退出',
        itemId:'exit'
    }]
});