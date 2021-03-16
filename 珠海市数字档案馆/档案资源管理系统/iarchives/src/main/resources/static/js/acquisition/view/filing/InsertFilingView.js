/**
 * Created by xuxf on 2019/10/8.
 */
Ext.define('Acquisition.view.filing.InsertFilingView', {
    extend: 'Ext.form.Panel',
    xtype: 'InsertFilingView',
    // layout:'fit',
    items:[{
        xtype:'fieldset',
        margin:'10',
        layout:'column',
        title: '位置设置',
        style:'background:#fff;padding-top:0px',
        items:[{
            columnWidth: 1,
            autoHeight:true,
            items:[{
                layout:'column',
                items:[{
                    //     columnWidth: 1,
                    //     xtype: 'radio',
                    //     itemId:'insertBehind',
                    //     inputValue: 'behind',
                    //     name:'insertPlace',
                    //     checked: true,
                    //     boxLabel:'插入最后'
                    // },{
                    columnWidth: 0.5,
                    xtype: 'radio',
                    checked: true,
                    itemId:'insertAnywhere',
                    inputValue: 'anywhere',
                    name:'insertPlace',
                    boxLabel:'插入位置'
                    // listeners: {
                    //     'render':function(){
                    //         var insertPlaceIndex = this.up('fieldset').down('[itemId=insertPlaceIndexText]');
                    //         insertPlaceIndex.disable(true);
                    //     },
                    //     'change':function(group,checked){
                    //         var insertPlaceIndex = this.up('fieldset').down('[itemId=insertPlaceIndexText]');
                    //         if(checked){
                    //             insertPlaceIndex.enable(true);
                    //         }else{
                    //             insertPlaceIndex.disable(true);
                    //         }
                    //     }
                    // }
                },{
                    //     columnWidth: .5,
                    //     xtype: 'radio',
                    //     itemId:'insertFront',
                    //     inputValue: 'front',
                    //     name:'insertPlace',
                    //     boxLabel:'插入首位'
                    // },{
                    columnWidth: .5,
                    xtype: 'textfield',
                    itemId:'insertPlaceIndexText',
                    labelWidth:200,
                    inputValue : true,
                    name:'insertPlaceIndex',
                    fieldLabel:'请指定插入预归档列表的序号',
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
                }]
            }]
        }]
    }],
    buttons:[{
        text:'确定',
        itemId:'checkInsert'
    }]
});