/**
 * Created by Administrator on 2020/10/21.
 */
Ext.define('Template.view.ListPropertyFormView',{
    extend: 'Ext.window.Window',
    xtype: 'listPropertyFormView',
    itemId: 'listPropertyFormViewId',
    closeToolText:'关闭',
    closeAction:'hide',
    width:'100%',
    height:'100%',
    layout:'border',
    items:[{
        xtype:'panel',
        margin: '20',
        region: 'center',
        layout:'fit',
        // width:'60%',
        // height:'75%',
        items:[{
            xtype:'templateTableFieldGridView'
        }]
    }, {
        xtype:'panel',
        margin: '20',
        region: 'east',
        bodyPadding: '50 5 50 5',
        items:[{
            width:'50%',
            height:'100%',
            region:'east',
            layout:'form',
            xtype:'fieldset',
            style:'background:#fff;padding-top:0px',
            title: '列表字段',
            autoHeight:true,
            labelWidth:60,
            labelAlign:'right',
            animCollapse :true,
            items:[{
                xtype: "form",
                style: "margin-left:20px",
                itemId:'isgridsetting',
                items: [{
                    xtype:'textfield',
                    name: "gwidth",
                    itemId:'gwidthID',
                    fieldLabel: "字段宽度",
                    regex: /^(0|[1-9][0-9]*)$/,
                    regexText : '请输入正确的数字'
                },{
                    xtype:'numberfield',
                    name: "gsequence",
                    itemId:'gsequenceID',
                    fieldLabel: "排序",
                    regex: /^(0|[1-9][0-9]*)$/,
                    regexText : '请输入正确的数字'
                },{
                    xtype: "checkbox",
                    name: "ghidden",
                    itemId:'ghiddenID',
                    fieldLabel: "是否隐藏字段"
                }]
            }]
        }]
    }],
    buttons: [{
        text: '保存',
        itemId: 'listPropertySaveBtnId'
    },{
        text:'返回',
        itemId:'back',handler:function () {
            this.up('window').close()
        }
    }]
})
