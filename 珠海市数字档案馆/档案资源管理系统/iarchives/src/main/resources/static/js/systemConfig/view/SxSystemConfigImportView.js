/**
 * Created by Administrator on 2019/4/19.
 */
Ext.define('SystemConfig.view.SxSystemConfigImportView', {
    extend: 'Ext.window.Window',
    xtype: 'sxSystemConfigImportView',
    itemId: 'sxSystemConfigImportViewid',
    title: '导入参数',
    width: 700,
    height: 260,
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
            fieldLabel: '',
            hidden: true,
            name: 'parentid',
            itemId: 'parentItemID'
        }, {
            xtype: 'tbtext'
        }, {
            xtype: 'fileuploadfield',
            itemId: 'importFileNameID',
            fileUpload: true,
            labelWidth: 80,
            width: 285,
            fieldLabel: '选择文件',
            buttonText: '浏览',
            buttonCfg: {
                iconCls: 'upload-icon'
            },
            anchor: '100%',
            emptyText: '请选择excel文件',
            name: 'fileImport',
            regex: /\.(xls|xlsx)$/,
            regexText: "请选择xls或者xlsx格式的Excel！",
            toolTip: true,
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
        }
        ]
    }],
    buttons: [{
        text: '导入',
        itemId: 'import'
    }, {
        text: '取消',
        itemId: 'cancel'
    }]
});