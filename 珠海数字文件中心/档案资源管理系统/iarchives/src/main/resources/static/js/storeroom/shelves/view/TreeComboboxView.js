/**
 * Created by RonJiang on 2017/12/01
 */
Ext.define('Shelves.view.TreeComboboxView', {
    extend: 'Ext.form.field.Picker',
    xtype:'TreeComboboxView',
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
    defaultValue: null,
    pathArray: [],
    selectNodeModel: 'all',
    maxHeight: 300,
    setValue: function (value) {
        if (value) {//注意：此处的判断会使id为0的值选中失效
            if (typeof value == 'number') {
                this.defaultValue = value;
            }
            this.callParent(arguments);
        }
        if(value ==''){
            this.defaultValue = '';
            this.callParent(arguments);
        }
    },
    initComponent: function () {
        var self = this;
        self.selectNodeModel = Ext.isEmpty(self.selectNodeModel) ? 'all' : self.selectNodeModel;

        Ext.apply(self, {
            fieldLabel: self.fieldLabel,
            labelWidth: self.labelWidth,
            nodeid:''// 选择树节点ID
        });

        self.callParent();
    },

    createPicker: function () {
        var self = this;

        self.picker = Ext.create('Ext.tree.Panel', {
            height: self.treeHeight==null ? 300 :self.treeHeight,
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
                var selModel = self.selectNodeModel;
                var isLeaf = record.get('leaf'); //是否为叶子节点
                var isRoot = record.get('root'); //是否为根节点
                var checked = record.get('checked'); //是否选中
                if (!self.multiSelect) {//单选
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
                    self.nodeid = record.raw.fnid;
                    while(record.parentNode.get('text')!='Root'){
                        fullname=record.parentNode.get('text')+'_'+fullname;
                        record=record.parentNode;
                    }
                    self.setValue(fullname);
                    self.eleJson = Ext.encode(record.raw);
                    self.collapse();
                }
                else if(self.multiSelect) {//多选
                    var selNodes = view.getChecked(); //选择的节点
                    var allselNodes =view.getStore(); //所有的节点
                    var organList = [];
                    Ext.each(selNodes, function (node) {
                        organList.push(node.data.fnid);
                    });
                    self.nodeid = organList;
                    self.submitValue = organList; //表单属性值


//------------------------------下拉框显示值（选择大于三个，显示前三个）start--------------------------------------
                    var depth = []; //获取深度数组
                    for(var i = 0; i < selNodes.length;i++) {
                        if(isInArray(depth,selNodes[i].raw.depth)){
                            continue;
                        }
                        else {
                            depth.push(selNodes[i].raw.depth);
                        }
                    }

                    sort(depth);//排序
                    var depthLength=depth.length;//深度数组长度
                    var depthNode ={};//选择节点对象

                    for(var i = 0; i < selNodes.length;i++) {
                        for(var j =0 ; j<depthLength; j++){
                            if(selNodes[i].raw.depth==depth[j]){
                                if(depthNode[j]==null || depthNode[j] =='') {
                                    depthNode[j] =  selNodes[i].raw.text;
                                }
                                else{
                                    depthNode[j] = depthNode[j] + "," + selNodes[i].raw.text;
                                }
                            }
                        }
                    }

                    var depthshow='';
                    for(var i =0 ; i<depthLength ;i++){
                        var depthNodeString = depthNode[i];
                        if(depthshow==null || depthshow =='') {
                            depthshow =  depthNodeString;
                        }
                        else{
                            depthshow =depthshow + ',' +depthNodeString;
                        }
                    }

                    if(selNodes.length == allselNodes.data.length)
                    {
                        self.setValue('全选');
                    }
                    else if(selNodes.length <= 3 && selNodes.length != 0){
                        self.setValue(depthshow);
                    }
                    else if(selNodes.length > 3){
                        var deptha = depthshow.split(',');
                        var depthshow = deptha.slice(0,3).join(',') + '....';
                        self.setValue(depthshow);
                    }
                    else if(selNodes.length == 0){
                        self.setValue('');
                    }
//------------------------------下拉框显示值（选择大于三个，显示前三个）end--------------------------------------

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

                    if (self.name == 'classifyId') {//分类下拉树，选择子节点默认选择父节点、
                        if (checked) {
                            node.expand();
                            var xhnode = node;
                            while (!xhnode.parentNode.data.root)
                            {
                                xhnode.parentNode.set('checked', checked);
                                xhnode = xhnode.parentNode;
                            }
                        }
                        if (!checked) {// 若节点的同类节点都没有选中，则其父节点也不会选中
                            var xhnode = node.parentNode;
                            while (!xhnode.data.root) { //是否为根节点
                                var state = true;
                                for (var i = 0; i < xhnode.childNodes.length; i++) {
                                    if (xhnode.childNodes[i].data.checked) {
                                        state = false;
                                    }
                                }
                                if (state) {
                                    xhnode.set('checked', false);
                                }
                                xhnode = xhnode.parentNode;
                            }
                        }
                    }
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

function isInArray(arr,val){
    var testStr=','+arr.join(",")+",";
    return testStr.indexOf(","+val+",")!=-1;
}

function sort(arr) {
    var s;
    //升序
    for (var i = 1; i < arr.length; i++) {
        for (var j = i; j > 0; j--) {
            if (arr[j] < arr[j - 1]) {
                s = arr[j];
                arr[j] = arr[j - 1];
                arr[j - 1] = s;
                //console.log(arr);//可以打印出来每一个改变的步骤 　　　　
            }
        }
    }
}