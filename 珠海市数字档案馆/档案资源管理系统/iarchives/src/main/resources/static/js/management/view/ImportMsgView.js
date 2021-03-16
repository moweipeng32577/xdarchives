/**
 * Created by Administrator on 2019/3/15.
 */


Ext.define('Management.view.ImportMsgView', {
    labelid: '',
    extend: 'Ext.window.Window',
    xtype: 'importMsgView',
    itemId: 'importMsgViewId',
    frame: true,
    resizable: true,
    flag: '',
    title: '选择判断重复的处理方式',
    width: 400,
    modal: true,
    closeToolText: '关闭',
    preview:'',//用于保存次层view
    impType:'',//用于保存单选参数
    items: [
        {
            xtype: 'form',
            modelValidation: true,
            bodyPadding: '20 30 10 70',
            layout: 'column',
            items: [{
                columnWidth: .5,
                xtype: 'radio',
                name: 'type',
                boxLabel: '不处理重复条目',
                inputValue: 'NotProcessed'
            }, {
                columnWidth: .4,
                xtype: 'radio',
                name: 'type',
                boxLabel: '覆盖重复条目',
                inputValue: 'Coverage'
            }]
        }
    ],

    buttons: [
        {text: '导入', itemId: 'import'},
        {
            text: '取消导入',
            itemId: 'setLabelClose',
            handler: function (btn) {
                btn.up('window').close();
            }
        }
    ]/*,
    listeners: {
        render: function (win) {
            var NotProcessed = win.down('[itemId=NotProcessed]');
            var Coverage = win.down('[itemId=Coverage]');
            NotProcessed.on('change', function (node, checked) {
                if (NotProcessed) {
                    Coverage.hidden(false);
                    //Coverage.setValue(checked);
                }
                if(Coverage){
                    NotProcessed.setDisabled(false);
                }
            });
        }
    }*/
});