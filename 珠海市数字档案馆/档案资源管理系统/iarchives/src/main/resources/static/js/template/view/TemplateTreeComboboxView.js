/**
 * Created by tanly on 2017/11/9 0009.
 */
Ext.define('Template.view.TemplateTreeComboboxView', {
    xtype:'templateTreeComboboxView',
    extend: 'Ext.form.field.Picker',
    requires: ['Ext.tree.Panel'],
    alias: ['widget.comboboxtree'],
    multiSelect: true,
    multiCascade: true,
    rootVisible: false,
    displayField: 'text',
    emptyText: '',
    submitValue: '',
    url: '',
    extraParams:'',
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
        if(value ==''){
            this.defaultValue = '';
            this.submitValue = ''; //form.submit 表单属性值
            var treepanel = this.picker;
            var nodes = treepanel.getChecked();
            for (var i = 0; i < nodes.length; i++) {
                var item = nodes[i];
                item.set("checked", false);
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
            height: self.treeHeight == null ? 350 : self.treeHeight,
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
            itemclick: function (view, recode, item, index, e, object) {
                var selModel = self.selectNodeModel;
                var isLeaf = recode.get('leaf');
                var isRoot = recode.get('root');
                var view = self.picker.getView();
                if (!self.multiSelect) {
                    if ((isRoot) && selModel != 'all') {
                        return;
                    } else if (selModel == 'exceptRoot' && isRoot) {
                        return;
                    } else if (selModel == 'folder' && isLeaf) {
                        return;
                    } else if (selModel == 'leaf' && !isLeaf) {
                        var expand = recode.get('expanded');
                        if (expand) {
                            view.collapse(recode);
                        } else {
                            view.expand(recode);
                        }
                        return;
                    }
                    self.submitValue = recode.get('id');

                    var fullname=recode.get('text');
                    while(recode.parentNode.get('text')!='Root'){
                        fullname=recode.parentNode.get('text')+'_'+fullname;
                        recode=recode.parentNode;
                    }
                    self.setValue(fullname);
                    self.eleJson = Ext.encode(recode.raw);
                    self.collapse();
                }else if(self.multiSelect){//多选
                    var selNodes = view.getChecked(); //选择的节点
                    var allselNodes =view.getStore(); //所有的节点
                    var List = [];
                    var fullnames = [];
                    Ext.each(selNodes,function (node) {
                        if(node.data.text.indexOf("gray") != -1){
                            node.editable=false;
                            node.set('checked', false);
                        }else{
                            // if(node.data.cls=='file'){
                            List.push(node.data.fnid);
                            var fullname = node.data.text;
                            while(node.parentNode.get('text')!='Root'){
                                if(node.parentNode.get('text').indexOf("gray") != -1){
                                    fullname=node.parentNode.get('text').split('>')[1].split('<')[0]+'_'+fullname;
                                }else {
                                    fullname=node.parentNode.get('text')+'_'+fullname;
                                }
                                node=node.parentNode;
                            }
                            fullnames.push(fullname);
                            // }
                        }
                    });
                    self.nodeid = List;
                    self.submitValue = List; //表单属性值

                    //------------------------------下拉框显示值（显示一个）start--------------------------------------

                    if(selNodes.length == allselNodes.data.length)
                    {
                        self.setValue('全选');
                    }
                    else if(List.length == 1){
                        self.setValue(fullnames);
                    }
                    else if(List.length > 1){
                        var showname = fullnames[0] + '....';
                        self.setValue(showname);
                    }
                    else if(List.length == 0){
                        self.setValue('');
                    }
                    //------------------------------下拉框显示值（显示一个）end--------------------------------------
                }
            },
            render: function (view) {
                var setLoop = function (node, check) {
                    node.set('checked', check);
                    if (node.isNode) {
                        node.eachChild(function (child) {
                            setLoop(child, check);
                        });
                    }
                };

                view.on('checkchange', function (node, checked) {
                    node.eachChild(function (child) { //选择父节点默认选择全部子节点、
                        child.set("checked", checked);
                        setLoop(child, checked);
                    });

                    // if (self.name == 'targetSelectItem') {//下拉树，选择子节点默认选择父节点、
                    //     if (checked) {
                    //         node.expand();
                    //         var xhnode = node;
                    //         while (!xhnode.parentNode.data.root)
                    //         {
                    //             xhnode.parentNode.set('checked', checked);
                    //             xhnode = xhnode.parentNode;
                    //         }
                    //     }
                    //     if (!checked) {// 若节点的同类节点都没有选中，则其父节点也不会选中
                    //         var xhnode = node.parentNode;
                    //         while (!xhnode.data.root) { //是否为根节点
                    //             var state = true;
                    //             for (var i = 0; i < xhnode.childNodes.length; i++) {
                    //                 if (xhnode.childNodes[i].data.checked) {
                    //                     state = false;
                    //                 }
                    //             }
                    //             if (state) {
                    //                 xhnode.set('checked', false);
                    //             }
                    //             xhnode = xhnode.parentNode;
                    //         }
                    //     }
                    // }
                }, view);
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
                this.proxy.extraParams.pcid = st.raw.fnid;
            });
        },
        // render:function(self){
        //     var treeStore = self.findParentByType('templateCopyFormView').treeView.getStore();
        //     var records = [];
        //     treeStore.each(function(rc){
        //         if(rc.get('text')!=='数据分类'){
        //             records.push(rc.copy());
        //         }
        //     });
        //     var selectTreeStore  = Ext.create('Ext.data.TreeStore', {
        //         model: 'Template.model.TemplateTreeModel',
        //         proxy: {
        //             type: 'memory',
        //             data:records,
        //             reader: {
        //                 type: 'json',
        //                 expanded: true
        //             }
        //         }
        //     });
        //     self.store = selectTreeStore;
        // },

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