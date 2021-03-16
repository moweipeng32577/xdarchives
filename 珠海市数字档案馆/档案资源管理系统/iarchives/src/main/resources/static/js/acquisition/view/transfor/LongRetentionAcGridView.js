/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Acquisition.view.transfor.LongRetentionAcGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'longRetentionAcGridView',
    title: '移交验证',
    searchstore: [
        {item: 'title', name: '题名'},
        {item: 'archivecode', name: '档号'}
    ],
    tbar: [{
        text: '查看验证明细',
        itemId: 'lookdetail',
        iconCls: 'fa fa-eye'
    }, '-',{
        text: '移交',
        itemId: 'transforTwo',
        iconCls: 'fa fa-reply-all'
    }],
    store: 'LongRetentionGridStore',
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
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
        },
        {
            text: '封装状态',
            dataIndex: 'base',
            flex: 1,
            menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                var checkstatus = record.get('checkstatus');
                if (typeof (checkstatus) == 'undefined' || checkstatus == '' || checkstatus.indexOf('不通过') != -1) {
                    return '未封装';
                }
                else if (checkstatus.indexOf('通过') != -1) {
                    return "<span style=\"color:green\">已封装</span>"
                }
                else {
                    return checkstatus;
                }
            }
        }
    ],
    hasSelectAllBox:true
});