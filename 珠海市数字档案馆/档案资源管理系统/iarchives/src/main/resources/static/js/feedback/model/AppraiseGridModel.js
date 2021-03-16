/**
 * Created by Administrator on 2020/3/18.
 */

Ext.define('Feedback.model.AppraiseGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'feedbackid'},
        {name: 'askman', type: 'string'},
        {name: 'appraise', type: 'string'},
        {name: 'appraisetext', type: 'string'},
        {name: 'borrowdocid', type: 'string',convert:function (value) {
            if(typeof value == 'undefined'||value == ''){
                return "使用评价";
            }else{
                return "借阅评价";
            }
        }}
    ]
});
