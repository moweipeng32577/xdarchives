/**
 * Created by Administrator on 2020/3/3.
 */
Ext.define('Reservation.model.ReservationAdminsModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'yystate', type: 'string'},//预约状态
        {name: 'replier', type: 'string'},//回复者
        {name: 'replytime', type: 'string'},//回复时间
        {name: 'replycontent', type: 'string'},//回复内容
        {name: 'borrowcontent', type: 'string'},//查档内容
        {name: 'borrowman', type: 'string'},//预约者
        {name: 'yytime', type: 'string'},//预约时间
        {name: 'borroworgan', type: 'string'},//工作单位
        {name: 'borrowmantel', type: 'string'},//联系电话
        {name: 'certificatestype', type: 'string'},//证件类型
        {name: 'certificatenumber', type: 'string'},//证件号
        {name: 'djtime', type: 'string'},//登记时间
        {name: 'canceler', type: 'string'},//取消人
        {name: 'canceltime', type: 'string'}//取消时间
    ]
});