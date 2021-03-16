/**
 * Created by yl on 2020/6/28.
 */
Ext.define('Datareceive.view.DatareceivedResultGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'datareceivedResultGridView',
    itemId: 'datareceivedResultGridViewID',
    bodyBorder: false,
    store: 'DatareceivedResultGridStore',
    hasCloseButton: false,
    hasSearchBar: false,
    head: false,
    tbar: [
        {
            text: '查看验证明细',
            itemId: 'lookdetail',
            iconCls: 'fa fa-eye'
        }
    ],
    columns: [
        {text: '条目ID', dataIndex: 'entryid', flex: 2, menuDisabled: true},
        {text: '检测状态', dataIndex: 'checkstatus', flex: 1, menuDisabled: true},
        {
            text: '准确性',
            dataIndex: 'authenticity',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (typeof (value) == 'undefined' || value == '') {
                    return '未检测';
                } else if (value.indexOf('验证不通过') != -1) {
                    return "<span style=\"color:red\">验证不通过</span>"
                } else if (value.indexOf('验证通过') != -1) {
                    return "<span style=\"color:green\">验证通过</span>"
                } else {
                    return value;
                }
            }
        },
        {
            text: '完整性',
            dataIndex: 'integrity',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (typeof (value) == 'undefined' || value == '') {
                    return '未检测';
                } else if (value.indexOf('验证不通过') != -1) {
                    return "<span style=\"color:red\">验证不通过</span>"
                } else if (value.indexOf('验证通过') != -1) {
                    return "<span style=\"color:green\">验证通过</span>"
                } else {
                    return value;
                }
            }
        },
        {
            text: '可用性',
            dataIndex: 'usability',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (typeof (value) == 'undefined' || value == '') {
                    return '未检测';
                } else if (value.indexOf('验证不通过') != -1) {
                    return "<span style=\"color:red\">验证不通过</span>"
                } else if (value.indexOf('验证通过') != -1) {
                    return "<span style=\"color:green\">验证通过</span>"
                } else {
                    return value;
                }
            }
        },
        {
            text: '安全性',
            dataIndex: 'safety',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (typeof (value) == 'undefined' || value == '') {
                    return '未检测';
                } else if (value.indexOf('验证不通过') != -1) {
                    return "<span style=\"color:red\">验证不通过</span>"
                } else if (value.indexOf('验证通过') != -1) {
                    return "<span style=\"color:green\">验证通过</span>"
                } else {
                    return value;
                }
            }
        }
    ]
});