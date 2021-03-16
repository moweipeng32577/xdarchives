/**
 * Created by Administrator on 2020/3/5.
 */

Ext.define('Ext.ux.Datetimefield', {
    xtype:'datetimefield',
    extend:'Ext.form.field.Date',
    alias: 'widget.datetimefield',
    requires: ['Ext.ux.DatetimePicker'],
    format : "Y-m-d H:i:s",
    altFormats : "Y-m-d H:i:s",
    renderSecondBtnStr:true, //秒选择组件开关
    createPicker: function() {
        var me = this,
            format = Ext.String.format;
        var pickerConfig = {
            pickerField: me,
            ownerCt: me.ownerCt,
            renderTo: document.body,
            floating: true,
            hidden: true,
            focusOnShow: true,
            minDate: me.minValue,
            maxDate: me.maxValue,
            disabledDatesRE: me.disabledDatesRE,
            disabledDatesText: me.disabledDatesText,
            disabledDays: me.disabledDays,
            disabledDaysText: me.disabledDaysText,
            format: me.format,
            showToday: me.showToday,
            startDay: me.startDay,
            renderSecondBtnStr:me.renderSecondBtnStr,
            minText: format(me.minText, me.formatDate(me.minValue)),
            maxText: format(me.maxText, me.formatDate(me.maxValue)),
            listeners:{
                scope:me,
                yes: me.onSelect,
                select:me.select,
                close:me.onClose,
                blur:me.onBlur,
                showPicker:me.expand
            },
            keyNavConfig: {
                esc: function() {
                    me.defCollapse();
                }
            }
        };
        return new Ext.ux.DatetimePicker(pickerConfig);
    },onSelect:function(m, d){
        var me = this;
        me.setValue(d);
        me.fireEvent('select', me, d);
        me.defCollapse();
    },select:function(){

    },collapse:function(){

    },defCollapse: function() {
        var me = this;
        if (me.isExpanded && !me.isDestroyed && !me.destroying) {
            var openCls = me.openCls,
                picker = me.picker,
                aboveSfx = '-above';
            picker.hide();
            me.isExpanded = false;
            me.bodyEl.removeCls([openCls, openCls + aboveSfx]);
            picker.el.removeCls(picker.baseCls + aboveSfx);
            // remove event listeners
            // me.hideListeners.destroy();
            Ext.un('resize', me.alignPicker, me);
            me.fireEvent('collapse', me);
            me.onCollapse();
        }
    },onBlur: function(e) {
        var me = this,
            v = me.rawToValue(me.getRawValue());
        if (Ext.isDate(v)) {
            me.setValue(v);
        }
        me.defCollapse();
        me.callParent([e]);
    },onClose:function(){
        this.defCollapse();
    }
});
