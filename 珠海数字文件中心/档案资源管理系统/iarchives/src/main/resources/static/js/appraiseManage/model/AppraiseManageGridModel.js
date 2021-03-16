/**
 * Created by Administrator on 2020/3/23.
 */


Ext.define('AppraiseManage.model.AppraiseManageGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'feedbackid'},
        {name: 'askman', type: 'string'},
        {name: 'appraise', type: 'string'},
        {
            name: 'appraisestar', type: 'string', convert: function (value,record) {
            if (record.data.appraise=='无可挑剔') {
                return "5星";
            } else if (record.data.appraise=='非常满意'){
                return "4星";
            } else if (record.data.appraise=='满意'){
                return "3星";
            } else if (record.data.appraise=='一般'){
                return "2星";
            }else{
                return "1星";
            }
        }
        },
        {name: 'appraisetext', type: 'string'},
        {
            name: 'borrowdocid', type: 'string', convert: function (value) {
            if (typeof value == 'undefined' || value == '') {
                return "使用评价";
            } else {
                return "借阅评价";
            }
        }
        }
    ]
});
