/**
 * Created by RonJiang on 2017/11/29 0029.
 */
Ext.define('Management.view.ManagementFilingView', {
    xtype: 'managementfiling',
    extend: 'Ext.tab.Panel',
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,
    items: [{
        title: '归档设置',
        itemId: 'gdszId',
        xtype: 'panel',
        layout: 'card',
        activeItem: 0,
        items: [{//归档第一步：表单窗口
            layout: 'fit',
            itemId: 'filingFirstStep',
            items: [{
                aout: 'form',
                scrollable: true,
                xtype: 'form',
                items: [{
                    layout: 'column',
                    labelWdith: '200',
                    items: [{
                        columnWidth: 0.6,
                        xtype: 'managementTreeComboboxView',
                        fieldLabel: '档案分类',
                        afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                        //width: '80%',
                        editable: false,
                        url: '/nodesetting/getYGDNodeByParentId',
                        extraParams: {pcid: ''},//根节点的ParentNodeID为空，故此处传入参数为空串
                        allowBlank: false,
                        name: 'nodename',
                        itemId: 'nodenameitemid',
                        margin: '40 20 5 50'
                    }, {
                        columnWidth: 0.2,
                        xtype: 'radiogroup',
                        itemId: 'radioGroupIds',
                        margin: '30 20 0 50',
                        fieldLabel: '根据档号重命名电子文件名',
                        width: 100,
                        items: [{
                            //padding: '0 20 0 80',
                            name: 'rename',
                            inputValue: 'true',
                            boxLabel: '是'
                        }, {
                            name: 'rename',
                            inputValue: 'false',
                            boxLabel: '否',
                            checked: true
                        }]
                    }]
                }, {
                    xtype: 'fieldset',
                    height: 80,
                    margin: '20 110 5 20',
                    title: '说明',
                    layout: 'fit',
                    items: [{
                        xtype: 'label',
                        style: 'font-size:18px;color:red;line-height:18px',
                        margin: '0 0 0 5',
                        html: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;选取需要归档的档案分类，点击“下一步”进入归档预览界面，点击“返回”关闭归档窗口。若选择分类节点的模板或档号设置不正确，将无法进行下一步归档操作，请在“系统设置”-“模板维护”中设置该节点的模板及档号。'
                    }]
                }, {
                    xtype: 'fieldset',
                    itemId: 'ordertxtId',
                    height: 350,
                    margin: '20 110 20 20',
                    title: '当前归档顺序',
                    layout: 'fit',
                    style: 'background:#fff;padding-top:0px',
                    collapsible: true,
                    collapsed: true,
                    autoScroll: true,
                    items: [{
                        xtype: 'ordersettingSelectedFormView',
                        itemId: 'ordersettingSelectedFormId'
                    }]
                }]
            }]

        }],
        buttons: [{
            text: '下一步',
            itemId: 'filingNextStepBtn'
        }, '-', {
            text: '返回',
            itemId: 'filingBackBtn'
        }]
    }, {
        title: '预归档',
        itemId: 'ygdId',
        xtype: 'panel',
        layout: 'card',
        items: [{//归档第二步：列表窗口
            itemId: 'filingSecondStep',
            layout: 'border',
            items: [{
                region: 'north',
                xtype: 'dynamicfilingform',
                flex: 1,
                itemId: 'dynamicfilingform',
                calurl: '/management/getCalValue'
            }, {
                region: 'center',
                itemId: 'ylId',
                xtype: 'entrygrid',
                flex: 2,
                templateUrl: '/template/changeGrid',
                dataUrl: '/management/entryIndexes/',
                hasSearchBar: false,
                sortableColumns: false,//取消列排序
                tbar: [{
                    text: '保管期限调整',
                    itemId: 'retentionAdjust'
                }, '-', {
                    text: '取消预归档',
                    itemId: 'ygdBackBtn'
                }, '-', {
                    text: '修改',
                    itemId: 'ygdEditBtn'
                }, '-', {
                    iconCls: '',
                    itemId: "batchModifyID",
                    menu: [
                        {
                            text: '批量修改',
                            itemId: 'batchModifyModifyId'
                        }, '-', {
                            text: '批量替换',
                            itemId: 'batchModifyReplaceId'
                        }, '-', {
                            text: '批量增加',
                            itemId: 'batchModifyAddId'
                        }
                    ],
                    text: '批量操作'
                }, '-', {
                    text: '上移',
                    itemId: 'moveup'
                }, '-', {
                    text: '下移',
                    itemId: 'movedown'
                }, '-', {
                    columnWidth: .07,
                    xtype: 'button',
                    margin: '0 0 0 5',
                    text: '取消选择',
                    tooltip: '取消所有跨页选择项',
                    style: {
                        'background-color': '#f6f6f6 !important',
                        'border-color': '#e4e4e4 !important'
                    },
                    handler: function (btn) {
                        var grid = btn.up('[itemId=ygdId]').down('entrygrid');
                        for (var i = 0; i < grid.getStore().getCount(); i++) {
                            grid.getSelectionModel().deselect(grid.getStore().getAt(i));
                        }
                        grid.acrossSelections = [];
                    }
                }, '-',{
                    xtype: 'label',
                    text: '当前归档排序: ',
                    itemId: 'orderTxtLabelYgdId',
                    style: {
                        color: 'blue',
                        'font-size': '17px',
                        'font-weight': 'bold'
                    }
                }, '-', {
                    xtype: 'label',
                    text: '',
                    itemId: 'labelTxtGdIds',
                    style: {
                        color: 'blue',
                        'font-size': '17px',
                        'font-weight': 'bold'
                    }
                }]
            }]
        }],
        buttons: [ /* {
            xtype: 'label',
            text: '',
            itemId: 'labelTxtGdIds',
            style: {
                color: 'blue',
                'font-size': '17px',
                'font-weight': 'bold'
            }
        }, '-',*/ {
            text: '归档',
            itemId: 'filingBtn'
        }, '-', {
            text: '上一步',
            itemId: 'filingpreviousStepBtn'
        }, '-', {
            text: '返回',
            itemId: 'filingBackTwoBtn'
        }]
    }, {
        title: '未归',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId: 'wgNodeId',
            xtype: 'entrygrid',
            hasCloseButton: false,
            hasCancelButton: true,
            dataUrl: '/management/entriesWg',
            tbar: {
                items: [
                    {
                        text: '加入预归档',
                        iconCls: 'fa fa-plus-circle',
                        itemId: 'addGdBtn'
                    }, '-', {
                        text: '插入预归档',
                        iconCls: 'fa fa-plus-circle',
                        // itemId: 'insertGdBtn',
                        menu: [
                            {
                                text: '插入首位',
                                itemId:'insertFront',
                                inputValue: 'front',
                                name:'insertPlace'
                            }, '-', {
                                text: '插入最后',
                                itemId:'insertBehind',
                                inputValue: 'behind',
                                name:'insertPlace'
                            }, '-', {
                                text: '插入位置',
                                itemId:'insertAnywhere',
                                inputValue: 'anywhere',
                                name:'insertPlace'
                            }
                        ]
                    }, '-', {
                        text: '预归档排序设置',
                        iconCls: 'fa fa-cogs',
                        itemId: 'addOrderSetBtn'
                    }, '-', {
                        xtype: 'label',
                        text: '当前归档排序:正序!',
                        itemId: 'orderTxtLabelId',
                        style: {
                            color: 'blue',
                            'font-size': '17px',
                            'font-weight': 'bold'
                        }
                    }
                ]
            },
            searchstore: {
                proxy: {
                    type: 'ajax',
                    url: '/template/queryName',
                    extraParams: {nodeid: 0},
                    reader: {
                        type: 'json',
                        rootProperty: 'content',
                        totalProperty: 'totalElements'
                    }
                }
            },
            hasSelectAllBox: true
        }]
    }]

});