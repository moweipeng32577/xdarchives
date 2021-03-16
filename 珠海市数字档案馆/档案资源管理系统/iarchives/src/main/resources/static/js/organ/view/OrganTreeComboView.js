/**
 * Created by tanly on 2019/1/10 0009.
 */
Ext.define('Organ.view.OrganTreeComboView', {
    xtype: 'organTreeComboView',
    extend: 'Ext.form.field.Picker',
    requires: ['Ext.tree.Panel'],
    alias: ['widget.comboboxtree'],
    multiSelect: false,
    multiCascade: true,
    rootVisible: true,
    displayField: 'text',
    emptyText: '',
    submitValue: '',
    url: '',
    extraParams: '',
    pathValue: '',
    defaultValue: null,
    pathArray: [],
    selectNodeModel: 'all',
    maxHeight: 400,
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
            height: self.treeHeight == null ? 200 : self.treeHeight,
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
                self.findParentByType('organImportView').down('[itemId=parentItemID]').setValue(record.get('fnid'));

                var selModel = self.selectNodeModel;
                var isLeaf = record.get('leaf');
                var isRoot = record.get('root');
                var view = self.picker.getView();
                if (!self.multiSelect) {
                    if ((isRoot) && selModel != 'all') {
                        return;
                    } else if (selModel == 'exceptRoot' && isRoot) {
                        return;
                    } else if (selModel == 'folder' && isLeaf) {
                        return;
                    } else if (selModel == 'leaf' && !isLeaf) {
                        var expand = record.get('expanded');
                        if (expand) {
                            view.collapse(record);
                        } else {
                            view.expand(record);
                        }
                        return;
                    }
                    self.submitValue = record.get('id');

                    var fullname = record.get('text');
                    while (record.parentNode !== null && record.parentNode.get('text') !== 'Root') {
                        fullname = record.parentNode.get('text') + '_' + fullname;
                        record = record.parentNode;
                    }
                    self.setValue(fullname);
                    self.eleJson = Ext.encode(record.raw);
                    self.collapse();
                }
            }
        });
        return self.picker;
    },
    listeners: {
        render: function (self) {
            self.store = Ext.create('Ext.data.TreeStore', {
                root: {
                    text: '机构管理',
                    expanded: true,
                    fnid: '0'
                },
                proxy: {type: 'ajax', url: self.url, extraParams: self.extraParams},
                autoLoad: true
            });
            self.store.addListener('nodebeforeexpand', function (st, rds, opts) {
                this.proxy.extraParams.pcid = st.raw.fnid;
            });
        },

        expand: function (field, eOpts) {
            var picker = this.getPicker();
            if (!this.multiSelect) {
                if (this.pathValue != '') {
                    picker.expandPath(this.pathValue, 'id', '/', function (bSucess, oLastNode) {
                        picker.getSelectionModel().select(oLastNode);
                    });
                }
            } else {
                if (this.pathArray.length > 0) {
                    for (var m = 0; m < this.pathArray.length; m++) {
                        picker.expandPath(this.pathArray[m], 'id', '/', function (bSucess, oLastNode) {
                            oLastNode.set('checked', true);
                        });
                    }
                }
            }
        }
    },
    clearValue: function () {
        this.setDefaultValue('', '');
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
    setPathValue: function (pathValue) {
        this.pathValue = pathValue;
    },
    setPathArray: function (pathArray) {
        this.pathArray = pathArray;
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