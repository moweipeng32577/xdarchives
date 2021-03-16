/**
 * Created by Rong on 2018/10/9.
 */
Ext.define('Acquisition.view.DataNodeComboView', {
    extend: 'Ext.form.field.Picker',
    xtype:'dataNodeComboView',
    requires: ['Ext.tree.Panel'],
    alias: ['widget.comboboxtree'],
    multiSelect: false,
    multiCascade: true,
    rootVisible: false,
    displayField: 'text',
    emptyText: '',
    submitValue: '',
    url: '',
    extraParams:'',
    defaultValue: null,
    pathArray: [],
    selectNodeModel: 'all',
    maxHeight: 200,

    setValue: function (value) {
        if (value) {//注意：此处的判断会使id为0的值选中失效
            if (typeof value == 'number') {
                this.defaultValue = value;
            }
            this.callParent(arguments);
        }
    },
    initComponent: function () {
        var self = this;
        self.selectNodeModel = Ext.isEmpty(self.selectNodeModel) ? 'all' : self.selectNodeModel;

        Ext.apply(self, {
            fieldLabel: self.fieldLabel,
            labelWidth: self.labelWidth
        });

        self.callParent();
    },

    createPicker: function () {
        var self = this;

        self.picker = Ext.create('Ext.tree.Panel', {
            height: self.treeHeight==null ? 200 :self.treeHeight,
            autoScroll: true,
            floating: true,
            focusOnToFront: false,
            shadow: true,
            ownerCt: this.ownerCt,
            useArrows: false,
            store: this.store,
            rootVisible: this.rootVisible,
            displayField: this.displayField,
            maxHeight: this.maxHeight
        });

        self.picker.on({
            itemclick: function (view, record, item, index, e, object) {
                self.up('form').getForm().findField('target').setValue(record.get('fnid'));
                var fullname = record.get('text');
                while(record.parentNode.get('text')!='Root'){
                    fullname=record.parentNode.get('text')+'_'+fullname;
                    record=record.parentNode;
                }
                self.setValue(fullname);
                self.collapse();
                self.fireEvent('change', self, record);
            }
        });
        return self.picker;
    },
    listeners: {
        render:function(self){
            self.store = Ext.create('Ext.data.TreeStore', {
                root: { expanded: true },
                proxy: { type: 'ajax', url: self.url, extraParams:self.extraParams},
                autoLoad: true
            });
            self.store.addListener('nodebeforeexpand', function (st, rds, opts) {
                this.proxy.extraParams.pid = st.raw.fnid;
            });
        }
    },
    clearValue: function () {
        this.setDefaultValue('','');
    },
    getEleJson: function () {
        if (this.eleJson == undefined) {
            this.eleJson = [];
        }
        return this.eleJson;
    },
    getSubmitValue: function () {
        if (this.submitValue == undefined) {
            this.submitValue = '';
        }
        return this.submitValue;
    },
    getDisplayValue: function () {
        if (this.value == undefined) {
            this.value = '';
        }
        return this.value;
    },
    getValue: function () {
        return this.getSubmitValue();
    },
    setDefaultValue: function (submitValue, displayValue) {
        this.submitValue = submitValue;
        this.setValue(displayValue);
        this.eleJson = undefined;
        this.pathArray = [];
    },
    alignPicker: function () {
        var me = this,
            picker,
            isAbove,
            aboveSfx = '-above';
        if (this.isExpanded) {
            picker = me.getPicker();
            if (me.matchFieldWidth) {
                picker.setWidth(me.bodyEl.getWidth());
            }

            if (picker.isFloating()) {
                picker.alignTo(me.inputEl, "", me.pickerOffset); // ""->tl
                isAbove = picker.el.getY() < me.inputEl.getY();
                me.bodyEl[isAbove ? 'addCls' : 'removeCls'](me.openCls + aboveSfx);
                picker.el[isAbove ? 'addCls' : 'removeCls'](picker.baseCls + aboveSfx);
            }
        }
    }
});