/**
 * Created by tanly on 2019/1/10 0009.
 */
Ext.define('Equipment.view.EquipmentImportView', {
    extend: 'Ext.window.Window',
    xtype: 'equipmentImportView',
    itemId: 'equipmentImportViewId',
    title: '导入设备信息',
    width: 700,
    height: 180,
    modal: true,
    closeToolText: '关闭',
    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 90%'
    },
    items: [{
        xtype: 'form',
        margin: '22',
        fileUpload: true,
        enctype: 'multipart/form-data',
        modelValidation: true,
        items: [{
            xtype: 'fileuploadfield',
            itemId: 'importFileNameID',
            fileUpload: true,
            labelWidth: 80,
            width: 285,
            fieldLabel: '选择文件',
            buttonText: '浏览',
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            buttonCfg: {
                iconCls: 'upload-icon'
            },
            anchor: '100%',
            emptyText: '请选择excel文件',
            name: 'fileImport',
            regex: /\.(xls|xlsx)$/,
            regexText: "请选择xls或者xlsx格式的Excel！",
            toolTip: true,
            name:'equipmentExcel',
            listeners: {
                change: function (me, v) {
                    var arr = v.split('.');
                    if (arr[arr.length - 1] != 'xls' && arr[arr.length - 1] != 'xlsx') {
                        XD.msg('请选择xls或者xlsx格式的Excel！');
                        me.setRawValue('');
                    } else {
                        me.setRawValue(v.substring(v.lastIndexOf('\\') + 1, v.length));
                    }
                }

            }
        }]
    }],
    buttons: [{
        text: '导入',
        itemId: 'import'
    }, {
        text: '取消',
        itemId: 'cancel'
    }]
});