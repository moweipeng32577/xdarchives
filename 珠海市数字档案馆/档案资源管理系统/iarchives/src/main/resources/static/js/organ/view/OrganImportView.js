/**
 * Created by tanly on 2019/1/10 0009.
 */
Ext.define('Organ.view.OrganImportView', {
    extend: 'Ext.window.Window',
    xtype: 'organImportView',
    itemId: 'organImportViewid',
    title: '导入机构',
    width: 700,
    height: 300,
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
            text:'123',
            itemId: 'parentItemID'
        },{
            xtype: 'organTreeComboView',
            fieldLabel: '父机构',
            editable: false,
            url: '/nodesetting/getOrganByParentId',
            extraParams: {pcid: '0'},
            allowBlank: false,
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            name: 'parentSelectItem',
            emptyText: '请选择节点',
            itemId: 'parentSelectItemID'
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
    },{
        region:'south',
        layout:'column',
        margin: '0 0 0 15',
        items: [
            {
                columnWidth: 1,
                xtype: 'label',
                itemId:'TIPS1',
                style:{color:'red'},
                text:'温馨提示：1.导入Excel中，机构类型的值域范围[unit，单位，department，部门]，机构状态的值域范围[1，启用，0，禁用]。2.单位或公司有重复机构名称的多层级导入，机构层级（标准001.001.002）必填，没有重复机构名称的，可以只填写上级机构',
                margin: '0 0 0 30'
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