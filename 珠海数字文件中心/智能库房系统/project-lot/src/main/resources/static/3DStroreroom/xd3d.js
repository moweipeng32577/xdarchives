/**
 * 欣档科技有限公司three.js封装库
 * 用于三维库房展示
 */
var xd3dObj = null;
var dbclick = 0;
var intersel = null;
var XD3D = {

    start:function(width) {
        this.width = document.documentElement.clientWidth;
        this.height = document.documentElement.clientHeight;
        this.objects = [];
        this.eventList = [];
        this.mouseClick = new THREE.Vector2();
        this.raycaster = new THREE.Raycaster();
        this.mtlLoader = new THREE.MTLLoader();

        this.initScene();
        this.initCamera(width);
        this.initRender();
        this.initLight();
        this.initControl();
        this.initStats();
        this.render();
        xd3dObj = this;
    },


    initScene:function() {
        this.scene = new THREE.Scene();
    },

    initCamera:function(width) {
        this.camera = new THREE.PerspectiveCamera(50, this.width/this.height, 1, 50000);
        this.camera.position.set(10, (this.height + this.width*width), this.width*width);
        this.camera.lookAt(0, 0, 0);
    },

    initRender:function() {
        this.renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true });
        this.renderer.setSize(this.width, this.height);
        document.getElementById('canvas').appendChild(this.renderer.domElement);
        this.renderer.shadowMap.enabled = true;
        this.renderer.shadowMapSoft = true;
        this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
        this.renderer.domElement.addEventListener('mousedown', this.onDocumentMouseDown, {passive: false});
        this.renderer.domElement.addEventListener('mousemove', this.onDocumentMouseMove, {passive: false});
    },


    initLight:function() {
        THREE.Light.shadowCameraVisible = true;
        var light = new THREE.AmbientLight(0xFFFFFF);
        this.scene.add(light);
    },

    initStats :function() {
        this.stats = new Stats();
        this.stats.domElement.style.position = 'absolute';
        this.stats.domElement.style.left = document.getElementById('canvas').clientWidth - 80+'px';
        document.body.appendChild(this.stats.domElement);
    },


    initControl:function() {
        this.controls = new THREE.OrbitControls(this.camera, this.renderer.domElement);
        //使用阻尼,指定是否有惯性
        this.controls.enableDamping = false;
        //动态阻尼系数 就是鼠标拖拽旋转灵敏度，阻尼越小越灵敏
        this.controls.dampingFactor = 0.9;
        //是否可以缩放
        this.controls.enableZoom = true;
        //是否自动旋转
        this.controls.autoRotate = false;
        //是否开启右键拖拽
        this.controls.enablePan = true;
    },

    render:function() {
         requestAnimationFrame(this.render.bind(this));
        this.controls.update();
        this.stats.update();
        this.renderer.render(this.scene, this.camera);
    },

    onDocumentMouseDown:function(event) {
        dbclick++;
        var _this = xd3dObj;
        setTimeout(function () { dbclick = 0}, 500);
        event.preventDefault();
        if (dbclick >= 2) {
            _this.raycaster.setFromCamera(_this.mouseClick, _this.camera);
            var intersects = _this.raycaster.intersectObjects(_this.objects);
            if (intersects.length > 0) {
                _this.controls.enabled = false;
                _this.SELECTED = intersects[0].object;
                if (_this.eventList != null && _this.eventList.dbclick != null && _this.eventList.dbclick.length > 0) {
                    for(var i = 0; i < _this.eventList.dbclick.length; i++ ){
                        var _obj = _this.eventList.dbclick[i];
                        if ("string" == typeof (_obj.obj_name)) {
                            if (_obj.obj_name == _this.SELECTED.name) {
                                _obj.obj_event(_this.SELECTED);
                            }
                        } else if (_obj.findObject!=null||'function' == typeof (_obj.findObject)) {
                            if (_obj.findObject(_this.SELECTED.name)) {
                                _obj.obj_event(_this.SELECTED);
                            }
                        }
                    }
                }
                _this.controls.enabled = true;
            }
        }
    },

    onDocumentMouseMove:function(event) {
        event.preventDefault();
        var _this = xd3dObj;
        _this.mouseClick.x = (event.clientX / _this.width) * 2 - 1;
        _this.mouseClick.y = -(event.clientY / _this.height) * 2 + 1;
        _this.raycaster.setFromCamera(_this.mouseClick, _this.camera);
        var intersects = _this.raycaster.intersectObjects(_this.objects);
        if (intersects.length > 0) {
            if(intersel != intersects[0].object && intersects[0].object.material.emissive){
                if(intersel)intersel.material.emissive.setHex(intersel.currentHex);
                intersel = intersects[0].object;
                intersel.currentHex = intersel.material.emissive.getHex();
                intersel.material.emissive.setHex(0xff0000);
            }else{
                if(intersel)intersel.material.emissive.setHex(intersel.currentHex);
                intersel = null;
            }
        }
    },


    addObject:function(_obj) {
        var _this = this;
        _this.objects.push(_obj);
        for(var i=0;i<_obj.children.length;i++){
            var child = _obj.children[i];
            if (child instanceof THREE.Mesh){
                _this.objects.push(child);
            }
        }
        _this.scene.add(_obj);
    },

    //加载obj对象
    loadObj:function(_obj){
        var _this = this;
        this.mtlLoader.setPath('obj/'+_obj.modal+'/');
        this.mtlLoader.load(_obj.modal+'.mtl', function(materials) {
            materials.preload();
            var objLoader = new THREE.OBJLoader();
            objLoader.setMaterials(materials);
            objLoader.setPath('obj/'+_obj.modal+'/');
            objLoader.load(_obj.modal+'.obj', function(object) {
                for(k in object.children){
                    // object.children[k].castShadow = true;
                    // object.children[k].receiveShadow = true;
                }
                object.name = _obj.name || _obj.modal;
                object.ddid = _obj.ddid;
                object.showtype = _obj.showtype;
                object.params = _obj.params;
                object.position.x = _obj.x;
                object.position.z = _obj.z;
                object.position.y = _obj.y;
                object.rotation.y = _obj.rotation * Math.PI;
                object.scale.set(_obj.scale, _obj.scale, _obj.scale);
                _this.addObject(object);
            });
        });
    },

    createSkin:function(flength,fwidth,_obj){
        var imgwidth = 128,imgheight=128;
        if (_obj.width != null&& typeof (_obj.width) != 'undefined') {
            imgwidth = _obj.width;
        }
        if (_obj.height != null && typeof (_obj.height) != 'undefined') {
            imgheight = _obj.height;
        }
        var texture = new THREE.TextureLoader().load(_obj.imgurl);
        var _repeat = false;
        if (_obj.repeatx != null && typeof (_obj.repeatx) != 'undefined' && _obj.repeatx==true) {
            texture.wrapS = THREE.RepeatWrapping;
            _repeat = true;
        }
        if (_obj.repeaty != null && typeof (_obj.repeaty) != 'undefined' && _obj.repeaty == true) {
            texture.wrapT = THREE.RepeatWrapping;
            _repeat = true;
        }
        if (_repeat) {
            texture.repeat.set(flength / imgheight, fwidth / imgheight);
        }
        return texture;
    },

    createSkinOptionOnj:function(_this, flength, fwidth, _obj, _cube, _cubefacenub){
        if (_this.commonFunc.hasObj(_obj)) {
            if (_this.commonFunc.hasObj(_obj.imgurl)) {
                return {
                    map: _this.createSkin(flength, fwidth, _obj),transparent:true
                }
            } else {
                if (_this.commonFunc.hasObj(_obj.skinColor)) {
                    _cube.faces[_cubefacenub].color.setHex(_obj.skinColor);
                    _cube.faces[_cubefacenub + 1].color.setHex(_obj.skinColor);
                }
                return {
                    vertexColors: THREE.FaceColors
                }
            }
        } else {
            return {
                vertexColors: THREE.FaceColors
            }
        }
    },

    initCube:function(_obj){
        _this = this;

        var _depth = _obj.depth;
        var _width = _obj.width;
        var _height = _obj.height;
        var _x = _obj.x || 0, _y = _obj.y || 0, _z = _obj.z || 0;
        var skinColor = _obj.style.skinColor || 0x98750f;
        var cubeGeometry = new THREE.CubeGeometry(_width, _height, _depth, 0, 0, 1);

        //六面颜色
        for (var i = 0; i < cubeGeometry.faces.length; i += 2) {
            var hex = skinColor || Math.random() * 0x531844;
            cubeGeometry.faces[i].color.setHex(hex);
            cubeGeometry.faces[i + 1].color.setHex(hex);
        }
        //六面纹理
        var skin_up_obj = {
            vertexColors: THREE.FaceColors
        }
        var skin_down_obj = skin_up_obj,
            skin_fore_obj = skin_up_obj,
            skin_behind_obj = skin_up_obj,
            skin_left_obj = skin_up_obj,
            skin_right_obj = skin_up_obj;
        var skin_opacity = 1;
        if (_obj.style != null && typeof (_obj.style) != 'undefined'
            && _obj.style.skin != null && typeof (_obj.style.skin) != 'undefined') {
            //透明度
            if (_obj.style.skin.opacity != null && typeof (_obj.style.skin.opacity) != 'undefined') {
                skin_opacity = _obj.style.skin.opacity;
            }
            //上
            skin_up_obj = _this.createSkinOptionOnj(_this, _depth, _width, _obj.style.skin.skin_up, cubeGeometry, 4);
            //下
            skin_down_obj = _this.createSkinOptionOnj(_this, _depth, _width, _obj.style.skin.skin_down, cubeGeometry, 6);
            //前
            skin_fore_obj = _this.createSkinOptionOnj(_this, _depth, _width, _obj.style.skin.skin_fore, cubeGeometry, 0);
            //后
            skin_behind_obj = _this.createSkinOptionOnj(_this, _depth, _width, _obj.style.skin.skin_behind, cubeGeometry, 2);
            //左
            skin_left_obj = _this.createSkinOptionOnj(_this, _depth, _width, _obj.style.skin.skin_left, cubeGeometry, 8);
            //右
            skin_right_obj = _this.createSkinOptionOnj(_this, _depth, _width, _obj.style.skin.skin_right, cubeGeometry, 10);
        }
        var cubeMaterialArray = [];
        //MeshLambertMaterial、MeshPhysicalMaterial、MeshStandardMaterial
        cubeMaterialArray.push(new THREE.MeshLambertMaterial(skin_fore_obj));
        cubeMaterialArray.push(new THREE.MeshLambertMaterial(skin_behind_obj));
        cubeMaterialArray.push(new THREE.MeshLambertMaterial(skin_up_obj));
        cubeMaterialArray.push(new THREE.MeshLambertMaterial(skin_down_obj));
        cubeMaterialArray.push(new THREE.MeshLambertMaterial(skin_right_obj));
        cubeMaterialArray.push(new THREE.MeshLambertMaterial(skin_left_obj));
        cube = new THREE.Mesh(cubeGeometry, cubeMaterialArray);
        cube.castShadow = true;
        cube.receiveShadow = true;
        cube.uuid = _obj.uuid;
        cube.name = _obj.name;
        cube.position.set(_x, _y, _z);
        if (_obj.rotation != null && typeof (_obj.rotation) != 'undefined' && _obj.rotation.length > 0) {
            for(var i = 0,len = _obj.rotation.length; i < len; i++){
                rotation_obj = _obj.rotation[i];
                switch (rotation_obj.direction) {
                    case 'x':
                        cube.rotateX(rotation_obj.degree);
                        break;
                    case 'y':
                        cube.rotateY(rotation_obj.degree);
                        break;
                    case 'z':
                        cube.rotateZ(rotation_obj.degree);
                        break;
                    case 'arb':
                        cube.rotateOnAxis(new THREE.Vector3(rotation_obj.degree[0], rotation_obj.degree[1], rotation_obj.degree[2]), rotation_obj.degree[3]);
                        break;
                }
            }
        }
        return cube;
    },

    initFloor:function(width, depth, x,y,z) {
        var floor = this.initCube({
            width:width,
            height:10,
            depth:depth,
            x:x,
            y:y,
            z:z,
            style: {
                skinColor: 0xBEC9BE,
                skin: {
                    skin_up: {
                        skinColor: 0x98750f,
                        imgurl: "obj/floor.jpg",
                        repeatx: true,
                        repeaty: true,
                        width: 128,
                        height: 128
                    },
                    skin_down: {
                        skinColor: 0xBEC9BE
                    },
                    skin_fore: {
                        skinColor: 0xBEC9BE
                    }
                }
            }
        });
        this.scene.add(floor);
        floor.receiveShadow=true;//地面接受阴影
    },

    initWall:function(width, x, z, rotation, option, add) {
        var wall = this.initCube({
            height:option.height || 350,
            width:width,
            depth:20,
            x:x,
            y:option.y || 180,
            z:z,
            rotation: [{ direction: 'y', degree: rotation * Math.PI}],
            style: {
                skin: {
                    skin_up: {
                        skinColor: 0xdddddd
                    },
                    skin_down: {
                        skinColor: 0xdddddd
                    },
                    skin_fore: {
                        skinColor: 0xb0cee0
                    },
                    skin_behind: {
                        skinColor: 0xb0cee0
                    },
                    skin_left: {
                        skinColor: 0xb0cee0
                    },
                    skin_right: {
                        skinColor: 0xdeeeee
                    }
                }
            }
        });
        if(add){
            this.scene.add(wall);
        }
        return wall;
    },

    initHole:function(wall,hole){
        var fobjBSP = new ThreeBSP(wall);
        var sobjBSP = new ThreeBSP(hole);
        var resultBSP = fobjBSP.subtract(sobjBSP);

        var cubeMaterialArray = [];
        for (var i = 0; i < 1; i++) {
            cubeMaterialArray.push(new THREE.MeshLambertMaterial({
                vertexColors: THREE.FaceColors
            }));
        }
        var result = resultBSP.toMesh(cubeMaterialArray);
        result.material.shading = THREE.FlatShading;
        result.geometry.computeFaceNormals();
        result.geometry.computeVertexNormals();
        result.material.needsUpdate = true;
        result.geometry.buffersNeedUpdate = true;
        result.geometry.uvsNeedUpdate = true;
        var _foreFaceSkin = null;
        for (var i = 0; i < result.geometry.faces.length; i++) {
            var _faceset = false;
            for (var j = 0; j < wall.geometry.faces.length; j++) {
                if (result.geometry.faces[i].vertexNormals[0].x === wall.geometry.faces[j].vertexNormals[0].x
                    && result.geometry.faces[i].vertexNormals[0].y === wall.geometry.faces[j].vertexNormals[0].y
                    && result.geometry.faces[i].vertexNormals[0].z === wall.geometry.faces[j].vertexNormals[0].z
                    && result.geometry.faces[i].vertexNormals[1].x === wall.geometry.faces[j].vertexNormals[1].x
                    && result.geometry.faces[i].vertexNormals[1].y === wall.geometry.faces[j].vertexNormals[1].y
                    && result.geometry.faces[i].vertexNormals[1].z === wall.geometry.faces[j].vertexNormals[1].z
                    && result.geometry.faces[i].vertexNormals[2].x === wall.geometry.faces[j].vertexNormals[2].x
                    && result.geometry.faces[i].vertexNormals[2].y === wall.geometry.faces[j].vertexNormals[2].y
                    && result.geometry.faces[i].vertexNormals[2].z === wall.geometry.faces[j].vertexNormals[2].z) {
                    result.geometry.faces[i].color.setHex(wall.geometry.faces[j].color.r * 0xff0000 + wall.geometry.faces[j].color.g * 0x00ff00 + wall.geometry.faces[j].color.b * 0x0000ff);
                    _foreFaceSkin = wall.geometry.faces[j].color.r * 0xff0000 + wall.geometry.faces[j].color.g * 0x00ff00 + wall.geometry.faces[j].color.b * 0x0000ff;
                    _faceset =true;
                }
            }
            if (_faceset == false){
                for(var j = 0; j < hole.geometry.faces.length; j++) {
                    if (result.geometry.faces[i].vertexNormals[0].x === hole.geometry.faces[j].vertexNormals[0].x
                        && result.geometry.faces[i].vertexNormals[0].y === hole.geometry.faces[j].vertexNormals[0].y
                        && result.geometry.faces[i].vertexNormals[0].z === hole.geometry.faces[j].vertexNormals[0].z
                        && result.geometry.faces[i].vertexNormals[1].x === hole.geometry.faces[j].vertexNormals[1].x
                        && result.geometry.faces[i].vertexNormals[1].y === hole.geometry.faces[j].vertexNormals[1].y
                        && result.geometry.faces[i].vertexNormals[1].z === hole.geometry.faces[j].vertexNormals[1].z
                        && result.geometry.faces[i].vertexNormals[2].x === hole.geometry.faces[j].vertexNormals[2].x
                        && result.geometry.faces[i].vertexNormals[2].y === hole.geometry.faces[j].vertexNormals[2].y
                        && result.geometry.faces[i].vertexNormals[2].z === hole.geometry.faces[j].vertexNormals[2].z
                        && result.geometry.faces[i].vertexNormals[2].z === hole.geometry.faces[j].vertexNormals[2].z) {
                        result.geometry.faces[i].color.setHex(hole.geometry.faces[j].color.r * 0xff0000 + hole.geometry.faces[j].color.g * 0x00ff00 + hole.geometry.faces[j].color.b * 0x0000ff);
                        _foreFaceSkin = hole.geometry.faces[j].color.r * 0xff0000 + hole.geometry.faces[j].color.g * 0x00ff00 + hole.geometry.faces[j].color.b * 0x0000ff;
                        _faceset = true;
                    }
                }
            }
            if (_faceset == false) {
                result.geometry.faces[i].color.setHex(_foreFaceSkin);
            }
        }
        result.castShadow = true;
        result.receiveShadow = true;
        return result;
    },

    initDoor:function(Lwidth,Lheight,Ldepth,Lx,Ly,Lz,Lrotation,Rwidth,Rheight,Rdepth,Rx,Ry,Rz,Rrotation){
        var floorleft = this.initCube({
            name:'leftdoor',
            width:Lwidth,
            height:Lheight,
            depth:Ldepth,
            x:Lx,
            y:Ly,
            z:Lz,
            rotation: [{ direction: 'y', degree: Lrotation * Math.PI}],
            style:{
                skin: {
                    opacity: 0.1,
                    skin_up: {
                        skinColor: 0x51443e
                    },
                    skin_down: {
                        skinColor: 0x51443e
                    },
                    skin_fore: {
                        skinColor: 0x51443e
                    },
                    skin_behind: {
                        skinColor: 0x51443e
                    },
                    skin_left: {
                        skinColor: 0x51443e,
                        imgurl: "obj/door_right.png"
                    },
                    skin_right: {
                        skinColor: 0x51443e,
                        imgurl: "obj/door_left.png"
                    }
                }
            }
        });
        var floorright = this.initCube({
            name:'rightdoor',
            width:Rwidth,
            height:Rheight,
            depth:Rdepth,
            x:Rx,
            y:Ry,
            z:Rz,
            rotation: [{ direction: 'y', degree: Rrotation * Math.PI}],
            style:{
                skin: {
                    opacity: 0.1,
                    skin_up: {
                        skinColor: 0x51443e
                    },
                    skin_down: {
                        skinColor: 0x51443e
                    },
                    skin_fore: {
                        skinColor: 0x51443e
                    },
                    skin_behind: {
                        skinColor: 0x51443e
                    },
                    skin_left: {
                        skinColor: 0x51443e,
                        imgurl: "obj/door_left.png"
                    },
                    skin_right: {
                        skinColor: 0x51443e,
                        imgurl: "obj/door_right.png"
                    }
                }
            }
        });
        this.addObject(floorleft);
        this.addObject(floorright);
    },

    initPlane:function(path,option,name){
        var loader = new THREE.TextureLoader();
        loader.setCrossOrigin(this.crossOrigin);
        var texture = loader.load(path);
        var MaterParam = {//材质的参数
            map: texture,
            side: THREE.FrontSide,
            transparent: 1,
            opacity: option.opacity || 1
        }
        var plane = new THREE.Mesh(
            new THREE.PlaneGeometry(option.width, option.height, 1, 1),
            new THREE.MeshBasicMaterial(MaterParam)
        );
        plane.name=name,
        plane.position.x = option.x || 0;
        plane.position.y = option.y || 0;
        plane.position.z = option.z || 0;
        plane.rotation.x = option.rotationx || 0;
        plane.rotation.y = option.rotationy || 0;
        plane.rotation.z = option.rotationz || 0;
        this.addObject(plane);
    },

    initLine:function(p1,p2,color){
        var geometry = new THREE.Geometry();
        p1 = new THREE.Vector3(p1.x, p1.y, p1.z);
        p2 = new THREE.Vector3(p2.x, p2.y, p2.z);
        geometry.vertices.push(p1);
        geometry.vertices.push(p2);
        var material = new THREE.LineBasicMaterial( { color: color || THREE.VertexColors } );
        var line = new THREE.Line( geometry, material );
        this.scene.add(line);
        return line;
    },

    //火灾动画xyz位置
    initFire: function(x,y,z,name) {
        var f = new fire();
        f.object.name = name;
        f.object.position.x = x;
        f.object.position.y = y;
        f.object.position.z = z;

        kf.addObject(f.object);

        function draw() {
            requestAnimationFrame(draw)
            f.update();
        }
        draw();
    },

    //漏水动画xyz位置
    initWater: function (length,width,height,x,y,z) {
        var geometry=new THREE.PlaneBufferGeometry(length,width,height);

        //水
        var flowMap=new THREE.TextureLoader().load('textures/water/1.jpg',function(map){

        });
        var water=new THREE.Water(geometry,{
            scale:0.2,
            textureWidth:200,
            textureHeiht:200,
            flowMap:flowMap
        });
        water.name = 'water';
        water.rotation.x=-Math.PI/2;
        water.position.set(x,y,z);
        // water.material.uniforms['color'].value.set('#ffffff');//水颜色
        kf.addObject(water);
    }

}

XD3D.commonFunc = {
    //判断对象
    hasObj: function (_obj) {
        if (_obj != null && typeof (_obj) != 'undefined') {
            return true;
        }else{
            return false;
        }
    },
    //查找对象
    findObject: function (_objname) {
        var _this = xd3dObj;
        var findedobj = null;
        for(var i=0;i<_this.objects.length;i++){
            _obj = _this.objects[i];
            if (_obj.name != null && _obj.name != '') {
                if (_obj.name == _objname) {
                    findedobj = _obj;
                    return findedobj;
                }
            }
        }
        return findedobj;
    },

    //查找档案对象
    findObjectByddId: function (filemodelId) {
        var _this = xd3dObj;
        var findedobj = [];
        for (var i = 0; i < _this.objects.length; i++) {
            _obj = _this.objects[i];
            if (_obj.ddid != null && _obj.ddid != '' && _obj.ddid != undefined) {
                if (_obj.ddid.indexOf(filemodelId) != -1) {
                    findedobj.push(_obj);
                    continue;
                }
            }
        }
        return findedobj;
    },

    //查找需要显示档案对象
    findObjectByshowtype: function (showtype) {
        var _this = xd3dObj;
        var findedobj = [];
        for (var i = 0; i < _this.objects.length; i++) {
            _obj = _this.objects[i];
            if (_obj.showtype != null && _obj.showtype != '' && _obj.showtype != undefined) {
                if (_obj.showtype == showtype) {
                    findedobj.push(_obj);
                    continue;
                }
            }
        }
        return findedobj;
    },


    //复制对象
    cloneObj: function (_objname, newparam) {
        var _this = XD3D;
        var fobj = _this.commonFunc.findObject(_objname);
        var newobj = newobj = fobj.clone();
        if (newobj.children != null && newobj.children.length > 1) {
            $.each(newobj.children, function (index, obj) {
                obj.name = newparam.childrenname[index];
                _this.objects.push(obj);
            });
        }
        //位置
        if (_this.commonFunc.hasObj(newparam.position)) {
            newobj.position.x = newparam.position.x;
            newobj.position.y = newparam.position.y;
            newobj.position.z = newparam.position.z;
        }
        //大小
        if (_this.commonFunc.hasObj(newparam.scale)) {
            newobj.scale.x = newparam.scale.x;
            newobj.scale.y = newparam.scale.y;
            newobj.scale.z = newparam.scale.z;
        }
        //角度
        if (_this.commonFunc.hasObj(newparam.rotation)) {
            $.each(newparam.rotation, function (index, rotation_obj) {
                switch (rotation_obj.direction) {
                    case 'x':
                        newobj.rotateX(rotation_obj.degree);
                        break;
                    case 'y':
                        newobj.rotateY(rotation_obj.degree);
                        break;
                    case 'z':
                        newobj.rotateZ(rotation_obj.degree);
                        break;
                    case 'arb':
                        newobj.rotateOnAxis(new THREE.Vector3(rotation_obj.degree[0], rotation_obj.degree[1], rotation_obj.degree[2]), rotation_obj.degree[3]);
                        break;
                }
            });
        }
        newobj.name = newparam.name;
        newobj.uuid = newparam.uuid;
        return newobj;
    },
    //设置表皮颜色
    setSkinColor: function (_objname, _color) {
        var _this = XD3D;
        var _obj = _this.commonFunc.findObject(_objname);
        if (_this.commonFunc.hasObj(_obj.material.emissive)) {
            _obj.material.emissive.setHex(_color);
        } else if (_this.commonFunc.hasObj(_obj.material.materials)) {
            if (_obj.material.materials.length > 0) {
                $.each(_obj.material.materials, function (index,obj) {
                    obj.emissive.setHex(_color);
                });
            }
        }
    },
    //添加图片标识
    addIdentification: function (_objname, _obj) {
        var _this = XD3D;
        var _fobj = _this.commonFunc.findObject(_objname);
        var loader = new THREE.TextureLoader();
        var texture = loader.load(_obj.imgurl, function () { }, undefined, function () { });
        var spriteMaterial = new THREE.SpriteMaterial({ map: texture, useScreenCoordinates: false });
        var sprite = new THREE.Sprite(spriteMaterial);
        sprite.name = _obj.name;
        sprite.position.x = _fobj.position.x + _obj.position.x;
        sprite.position.y = _fobj.position.y + _obj.position.y;
        sprite.position.z = _fobj.position.z + _obj.position.z;
        if (_this.commonFunc.hasObj(_obj.size)) {
            sprite.scale.set(_obj.size.x, _obj.size.y);
        } else {
            sprite.scale.set(1,1);
        }
        _this.addObject(sprite);
    },
    //添加文字
    makeTextSprite: function (_objname, parameters)
    {
        var _this = XD3D;
        var _fobj = _this.commonFunc.findObject(_objname);
        if ( parameters === undefined ) parameters = {};
        var fontface = parameters.hasOwnProperty("fontface") ? parameters["fontface"] : "Arial";
        var fontsize = parameters.hasOwnProperty("fontsize") ? parameters["fontsize"] : 18;
        var borderThickness = parameters.hasOwnProperty("borderThickness") ? parameters["borderThickness"] : 4;
        var textColor = parameters.hasOwnProperty("textColor") ?parameters["textColor"] : { r:0, g:0, b:0, a:1.0 };
        var message = parameters.hasOwnProperty("message") ? parameters["message"] : "helloXD3D";
        var x = parameters.hasOwnProperty("position") ? parameters["position"].x : 0;
        var y = parameters.hasOwnProperty("position") ? parameters["position"].y : 0;
        var z = parameters.hasOwnProperty("position") ? parameters["position"].z : 0;
        var canvas = document.createElement('canvas');
        var context = canvas.getContext('2d');
        context.font = "Bold " + fontsize + "px " + fontface;
        var metrics = context.measureText( message );
        var textWidth = metrics.width;
        context.lineWidth = borderThickness;
        context.fillStyle = "rgba("+textColor.r+", "+textColor.g+", "+textColor.b+", 1.0)";
        context.fillText(message, borderThickness, fontsize + borderThickness);
        var texture = new THREE.Texture(canvas)
        texture.needsUpdate = true;
        var spriteMaterial = new THREE.SpriteMaterial( { map: texture, useScreenCoordinates: false } );
        var sprite = new THREE.Sprite(spriteMaterial);
        sprite.position.x =_fobj.position.x + x;
        sprite.position.y = _fobj.position.y + y;
        sprite.position.z = _fobj.position.z + z;
        sprite.name = parameters.name;
        sprite.scale.set(0.5 * fontsize, 0.25 * fontsize, 0.75 * fontsize);
        _this.addObject(sprite);
    },

    setCamera:function(x,y,z) {
        xd3dObj.camera.position.set(x, y, z);
    }

};

//火焰模型

// class fire {
//     constructor(density =20, height = 300, r = 2) {
//         this.object = new THREE.Group();
//         this.fireballs = [];
//         this.height = height;
//         this.radius = r;
//         var texture = new THREE.TextureLoader().load();
//
//         this.fireMaterial = new THREE.ShaderMaterial({ //材质
//             uniforms: {
//                 blendPattern: { type: "c", value: texture }
//             },
//             transparent: true,
//         });
//
//         for(var i = 0; i< density; i++){
//             var geometry = new THREE.SphereGeometry(35, 32, 32 ); //圆圈
//             var mat = this.fireMaterial.clone();
//             mat.uniforms.blendPattern.value = texture;
//             mat.needsUpdate = true;
//             var sphere = new THREE.Mesh( geometry,  mat);
//             sphere.position.y = Math.random() * height * 3;
//             sphere.position.x = (0.5 - Math.random()) * this.radius;
//             sphere.position.z = (0.5 - Math.random()) * this.radius;
//             sphere.dirX = ((0.5-Math.random())*0.5);
//             sphere.dirY= 6;
//             sphere.dirZ = ((0.5-Math.random())*0.5);
//
//             this.fireballs.push(sphere);
//         }
//         this.object.add.apply(null,this.fireballs);
//     }
//
//     update() {
//         this.fireballs.forEach( function(ball){
//             ball.position.y += ball.dirY;
//             ball.position.x += Math.sin(ball.position.y)*ball.dirX;
//             ball.position.z += Math.cos(ball.position.y)*ball.dirZ;
//             if(ball.position.y > this.height) {
//                 ball.position.y = Math.random() * 0.1;
//                 ball.position.x = (0.5 - Math.random()) * this.radius;
//                 ball.position.z = (0.5 - Math.random()) * this.radius;
//             }
//
//             var p = 0.2 + (ball.position.y / this.height);
//             ball.scale.set(p,p,p);
//         })
//     }
//
//     clean(){
//         this.object.remove()
//     }
// }