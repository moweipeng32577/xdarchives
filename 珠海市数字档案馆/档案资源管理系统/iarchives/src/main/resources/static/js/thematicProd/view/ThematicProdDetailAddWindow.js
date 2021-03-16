/**
 * Created by yl on 2017/11/1.
 */
Ext.define('ThematicProd.view.ThematicProdDetailAddWindow', {
    extend: 'Ext.window.Window',
    xtype: 'thematicProdDetailAddWindow',
    width: 800,
    height: 270,
    minWidth: 800,
    minHeight: 270,
    closeToolText:'关闭',
    layout: 'fit',
    autoShow: true,
    modal: true,
    resizable: false,//是否可以改变窗口大小
    items: [{
        xtype: 'form',
        autoScroll: true,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        fieldDefaults: {
            labelWidth: 80
        },
        bodyPadding: 20,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '题名',
                    name:'title',
                    style: 'width: 90%'
                }]
            }, {
                columnWidth: .5,
                items: [{
                    xtype: 'datefield',
                    fieldLabel: '时间',
                    name:'filedate',
                    editable:false,
                    format: 'Y-m-d H:i:s',
                    style: 'width: 100%'
                }]
            }]
        },{
            layout: 'column',
            items: [{
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '责任者',
                    name:'responsibleperson',
                    style: 'width: 90%'
                }]
            }, {
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '文件编号',
                    name:'filecode',
                    style: 'width: 100%'
                }]
            }]
        },{
            layout: 'column',
            items: [{
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '主题词',
                    name:'subheadings',
                    style: 'width: 90%'
                }]
            }, {
                columnWidth: .35,
                items: [{
                    xtype: 'textfield',
                    itemId:'media',
                    fieldLabel: '电子文件',
                    style: 'width: 100%',
                    name:'mediatext',
                    editable:false
                }]
            }, {
                columnWidth: .06,
                style : 'text-align:center;',
                margin: '8 0 0 0',
                items: [{
                    xtype: 'label',
                    itemId:'mediacount',
                    text: '共0份',
                    style: 'width: 100%'
                }]
            }, {
                columnWidth: .09,
                items: [{
                    xtype: 'button',
                    itemId:'electronId',
                    text: '选择',
                    style: 'width: 100%'
                }]
            }]
        // }, {
        //     layout: 'column',
        //     items: [{
        //         columnWidth: .5,
        //         items: [{
        //             xtype: 'textfield',
        //             fieldLabel: '文件夹',
        //             name: 'chapter',
        //             itemId: 'chapterId',
        //             style: 'width: 90%',
        //             enableKeyEvents: true
        //         }]
        //     }, {
        //         columnWidth: .5,
        //         items: [{
        //             xtype: 'textfield',
        //             fieldLabel: '下级文件夹',
        //             itemId: 'sectionId',
        //             name: 'section',
        //             enableKeyEvents: true,
        //             style: 'width: 100%'
        //         }]
        //     }]
         }]
    }],
    buttons: [{
        itemId: 'saveBtnID',
        text: '保存'
    },{
        itemId: 'backBtnID',
        text: '返回'
    }]
});