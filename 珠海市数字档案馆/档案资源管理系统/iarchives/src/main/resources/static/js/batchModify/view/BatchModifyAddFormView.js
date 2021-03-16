/**
 * Created by RonJiang on 2018/2/3 0003.
 */
Ext.define('BatchModify.view.BatchModifyAddFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'batchModifyAddFormView',
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
            html:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;给字段值前面、后面或者中间某位置前，新增内容'
        }]
    },{
        region:'center',
        xtype:'fieldset',
        margin:'5 160 5 20',
        layout:'column',
        title: '添加设置',
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
            fieldLabel:'字段名称',
            allowBlank:false
        },{
            columnWidth:0.02,
            xtype:'displayfield',
            value:'<label style="color:#ff0b23;!important;">*</label>'
        },{
            columnWidth: .7,
            xtype: 'textfield',
            itemId:'searchContentText',
            name:'addcontent',
            labelWidth:100,
            inputValue : true,
            margin:'5 0 5 0',
            fieldLabel:'添加内容',
            allowBlank:false
        },{
            columnWidth:0.02,
            xtype:'displayfield',
            value:'<label style="color:#ff0b23;!important;">*</label>'
        },{
            columnWidth: .7,
            xtype: 'fieldset',
            title:'位置设置',
            style:'background:#fff;padding-top:0px',
            autoHeight:true,
            items:[{
                layout:'column',
                items:[{
                    columnWidth: .5,
                    xtype: 'radio',
                    itemId:'insertFront',
                    inputValue: 'front',
                    name:'insertPlace',
                    boxLabel:'前面添加'
                },{
                    columnWidth: .5,
                    xtype: 'textfield',
                    itemId:'insertPlaceIndexText',
                    labelWidth:110,
                    inputValue : true,
                    name:'insertPlaceIndex',
                    fieldLabel:'插入字符位置',
                    listeners: {
                        render: function(sender) {
                            new Ext.ToolTip({
                                target: sender.el,
                                trackMouse: true,
                                dismissDelay: 0,
                                anchor: 'buttom',
                                html: "请输入整数，例如：原字段内容为“ABC3.1”，添加内容为“0”，插入字符位置为“6”，结果变成“ABC3.01”"
                            });
                        }
                    }
                },{
                    columnWidth: 1,
                    xtype: 'radio',
                    itemId:'insertBehind',
                    inputValue: 'behind',
                    name:'insertPlace',
                    boxLabel:'后面添加'
                },{
                    columnWidth: 1,
                    xtype: 'radio',
                    itemId:'insertAnywhere',
                    inputValue: 'anywhere',
                    name:'insertPlace',
                    boxLabel:'插入添加',
                    listeners: {
                        'render':function(){
                            var insertPlaceIndex = this.up('fieldset').down('[itemId=insertPlaceIndexText]');
                            insertPlaceIndex.disable(true);
                        },
                        'change':function(group,checked){
                            var insertPlaceIndex = this.up('fieldset').down('[itemId=insertPlaceIndexText]');
                            if(checked){
                                insertPlaceIndex.enable(true);
                            }else{
                                insertPlaceIndex.disable(true);
                            }
                        }
                    }
                }]
            }]
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