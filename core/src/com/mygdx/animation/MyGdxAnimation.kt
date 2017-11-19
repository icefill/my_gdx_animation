package com.mygdx.animation
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.mygdx.anim_test.animation.Anim
import com.mygdx.anim_test.animation.Dir
import com.mygdx.anim_test.animation.ViewUnit
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.SerializationException
import com.mygdx.anim_test.animation.gdxArray


class MyGdxAnimation : ApplicationAdapter() {
    lateinit internal var batch: SpriteBatch
    lateinit internal var anim: Anim
    internal var time: Float=0f
    lateinit internal var rootTable : Table
    lateinit internal var stage:Stage
    lateinit internal var skin:Skin
    lateinit var viewUnitTables:Table
    lateinit var viewUnitWindow2:Window
    lateinit var viewUnitWindow:Window
    var selectedViewUnitIndex:Int=0

    lateinit var anchorsWindow:AnchorsWindow
    lateinit var selectedFrameWindow:Window
    lateinit var framesWindow:FramesWindow

    lateinit var group:FrameGroup


    lateinit var saveTextField :TextField
    lateinit var loadTextField :TextField

    val json = Json()
    lateinit var viewUnits :Array<ViewUnit?>
    lateinit var wholeViewUnits :Array<ViewUnit?>

    override fun create() {
        json.setUsePrototypes(false)
        stage = Stage(StretchViewport(864f*1.2f, 486f*1.2f))
        Gdx.input.inputProcessor = stage
        batch = SpriteBatch()
        skin= Skin(Gdx.files.internal("uiskin.json"))
        anim= Anim()


        val atlas= TextureAtlas("basic.atlas")
        val atlas2= TextureAtlas("weapons.atlas")

        wholeViewUnits= arrayOf(
            ViewUnit(16f,8f,8f,atlas.findRegion("head_dl"),atlas.findRegion("feet_ur"))
            ,ViewUnit(16f,8f,8f,atlas.findRegion("body_dl"),atlas.findRegion("body_dl"))
            ,ViewUnit(8f,4f,4f,atlas.findRegion("hand_dl"))
            ,ViewUnit(8f,4f,4f,atlas.findRegion("feet_dl"),atlas.findRegion("feet_dl"))
            ,ViewUnit(32f,16f,8f,atlas2.findRegion("sword1"),atlas.findRegion("sword1"))
            ,ViewUnit(32f,16f,8f,atlas2.findRegion("sword2"),atlas.findRegion("sword2"))
            ,ViewUnit(32f,16f,12f,atlas2.findRegion("shield1"),atlas.findRegion("shield1"))
            ,ViewUnit(32f,16f,12f,atlas2.findRegion("shield2"),atlas.findRegion("shield2"))
            ,ViewUnit(48f,24f,12f,atlas2.findRegion("spear1"),atlas.findRegion("shield1"))
            ,ViewUnit(32f,16f,16f,atlas2.findRegion("helm1"))
            ,ViewUnit(32f,16f,16f,atlas2.findRegion("helm2"))
        )

        viewUnits= arrayOfNulls<ViewUnit?>(15)
        viewUnits[0]=wholeViewUnits[0]
        viewUnits[1]=wholeViewUnits[1]
        viewUnits[2]=wholeViewUnits[2]
        viewUnits[3]=wholeViewUnits[3]
        viewUnits[4]=wholeViewUnits[4]


        group= FrameGroup(anim,viewUnits,true)
        viewUnitTables = Table(skin)

        selectedFrameWindow =Window("SelectedFrame",skin)
        anchorsWindow= AnchorsWindow(anim,skin,viewUnits)
        framesWindow= FramesWindow(anim,skin,viewUnits)

        val leftUpTable=Table()
        val rightUpTable=Table()
        val downTable=Table()

        rootTable= Table()

        var i=0
        viewUnitWindow2= Window("Choose part",skin)
        for (viewUnit in viewUnits){
            val viewUnitTable=ViewUnitTable(i,viewUnit,skin)
            viewUnitTables.add(viewUnitTable).size(48f).pad(5f)
            viewUnitTable.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float)
                {// open window
                    viewUnitWindow2.isVisible = true
                    selectedViewUnitIndex=viewUnitTable.index
                }
            })
            i++
        }

        viewUnitWindow2.isVisible = false
        i=0
        for (viewUnit in wholeViewUnits){
            val viewUnitTable=ViewUnitTable(i,viewUnit,skin)
            viewUnitWindow2.add(viewUnitTable).size(48f).pad(5f)
            viewUnitTable.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float)
                {// open window
                    viewUnits[selectedViewUnitIndex]=viewUnit
                    (viewUnitTables.children[selectedViewUnitIndex] as ViewUnitTable).viewUnit=viewUnit
                    viewUnitWindow2.isVisible = false
                }
            })
            i++
        }
        val viewUnitTable=ViewUnitTable(i,null,skin)
        viewUnitWindow2.add(viewUnitTable).size(48f).pad(5f)
        viewUnitTable.addListener( object: ClickListener(){
            override fun clicked(e: InputEvent, x:Float, y:Float)
            {// open window
                viewUnits[selectedViewUnitIndex]=null
                (viewUnitTables.children[selectedViewUnitIndex] as ViewUnitTable).viewUnit=null
                viewUnitWindow2.isVisible = false
            }
        })


        val filesWindow=Window("Files",skin)
        val saveButton= TextButton("Save",skin)
        val loadButton= TextButton("Load",skin)
        saveTextField=TextField("",skin)
        loadTextField=TextField("",skin)

        filesWindow.add(saveButton).pad(3f)
        filesWindow.add(saveTextField).pad(3f).row()
        filesWindow.add(loadButton).pad(3f)
        filesWindow.add(loadTextField).pad(3f).row()

        saveButton.addListener( object: ClickListener(){
            override fun clicked(e: InputEvent, x:Float, y:Float) {
                saveFile(saveTextField.text)
            }
        })
        loadButton.addListener( object: ClickListener(){
            override fun clicked(e: InputEvent, x:Float, y:Float) {
                loadFile(loadTextField.text)
            }
        })

        leftUpTable.add(selectedFrameWindow).height(182f).grow().pad(5f).row()
        leftUpTable.add(filesWindow).grow().pad(5f)

        selectedFrameWindow.add(group).center()

        viewUnitWindow= Window("ViewUnits",skin)
        val viewUnitScrollPane = ScrollPane(viewUnitTables,skin)
        viewUnitScrollPane.setFadeScrollBars(false)
        viewUnitWindow.add(viewUnitScrollPane).height(90f)
        rightUpTable.add(viewUnitWindow).height(95f).grow().pad(5f).row()
        rightUpTable.add(framesWindow).grow().pad(5f).colspan(2).row()
        downTable.add(anchorsWindow).height(161f).pad(5f).colspan(2).grow().row()

        rootTable.add(leftUpTable).grow().pad(5f)
        rootTable.add(rightUpTable).grow().pad(5f).row()
        rootTable.add(downTable).grow().pad(5f).colspan(2)

        rootTable.setFillParent(true)
        stage.addActor(rootTable)
        stage.addActor(viewUnitWindow2)
        viewUnitWindow2.setFillParent(true)

    }
    fun loadFile(fileName: String) {
        try {
             json.fromJson(Anim::class.java, Gdx.files.local(fileName))
        } catch (e: SerializationException){
            loadTextField.text = "ERROR, NO FILE !!!"
            null
        }?.let{
            anim=it
            group.anim = anim
            framesWindow.anim=anim
            anchorsWindow.anim=anim
            //anchorsWindow.initializeAnchorTables()
        }
    }

    fun saveFile(fileName: String) {
        val fileHandle =Gdx.files.local(fileName)
        fileHandle.writeString(json.prettyPrint(anim),false)
    }
    fun setCurrentFrameIndex(frameIndex:Int) {
        anchorsWindow.setAnchorTables(frameIndex)
        group.frameIndex=frameIndex
    }
    override fun render() {
        time+=Gdx.graphics.deltaTime
        if (framesWindow.currentFrameIndexChanged) {
            setCurrentFrameIndex(framesWindow.currentFrameIndex)
        }
        if (anchorsWindow.anchorModified) {
            group.initializeActors()
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()


    }
    override fun resize(width:Int,height :Int) {
        stage.viewport.update(width,height,true)
    }

    override fun dispose() {
        batch.dispose()
    }

    class ViewUnitTable(internal var index: Int, viewUnit: ViewUnit?, skin:Skin): Button(skin){
        internal var viewUnit: ViewUnit?= viewUnit
                    set(viewUnit) {
                        field=viewUnit
                    }
        init {
            this.add(index.toString()).left().row()
            this.add().size(32f)
        }
        override fun act(delta:Float) {
            //this.setBounds(x,y,64f,64f)
        }


        override fun draw(batch: Batch, delta:Float){
            super.draw(batch,delta)
            viewUnit?.draw(Dir.DL,batch,x+this.width*.5f,y+viewUnit!!.anchorY +this.height*.5f-20f,0f)
            //viewUnit.draw(Dir.DL,batch,x+16f,y+16f,0f)

        }
    }

    class ObjActor(var anim: Anim, var viewUnits: Array<ViewUnit?>, val fixedFrame:Boolean=false) : Actor(){
        var time=0f
        var frameIndex=0
        override fun act(delta:Float){
            super.act(delta)
            time+=Gdx.graphics.deltaTime
        }
        override fun draw(batch:Batch,delta: Float){
            super.draw(batch,delta)
            if (fixedFrame) {
                anim.draw(batch, x, y, frameIndex, viewUnits)
            }
            else {
                anim.draw(batch, x, y, time, viewUnits)
            }
        }
    }

    class FrameGroup(anim:Anim, viewUnits: Array<ViewUnit?>, val forEditing:Boolean): Group(){
        var anim=anim
            set(anim) {
                field=anim
                objActor.anim=anim
                this.frameIndex=0
                if (forEditing) initializeActors()
            }
        var objActor= ObjActor(anim,viewUnits,forEditing)
        val partActors =ArrayList<Actor>()
        val root_x=0f
        val root_y=-10f
        var frameIndex=0
                set(frameIndex){
                    field=frameIndex
                    if (forEditing) objActor.frameIndex=frameIndex
                }
        var touchX=0f
        var touchY=0f
        init{
            objActor.setPosition(root_x,root_y)
            initializeActors()
            this.setScale(3f)
        }
        fun initializeActors(){
            partActors.clear()
            this.clearChildren()
            this.addActor(objActor)
            if (forEditing) initializeMovers()
        }
        fun initializeMovers() {
            for (i  in 0..anim.anchorOrder.size-1) {
                val partActor= Actor()
                val anchorCoord=anim.anchors[anim.anchorOrder[i]].anchorCoords[frameIndex]
                partActors.add(partActor)
                partActor.setBounds(anchorCoord.x-2+root_x,anchorCoord.y-2+root_y,4f,4f)
                partActor.setOrigin(2f,2f)
                this.addActor(partActor)
                partActor.addListener( object: InputListener(){
                    override fun touchDown(event :InputEvent,x:Float,y:Float,pointer:Int,button:Int):Boolean{
                        touchX=x
                        touchY=y
                        return true
                    }
                    override fun touchDragged(event :InputEvent,x:Float,y:Float,pointer:Int){
                        val dx=(x-touchX).toInt()
                        val dy=(y-touchY).toInt()
                        if (dx!=0 || dy!=0) {
                            anim.anchors[anim.anchorOrder[i]].anchorCoords[frameIndex].x+=dx
                            anim.anchors[anim.anchorOrder[i]].anchorCoords[frameIndex].y+=dy
                        }
                    }

                })
            }
            this.setDebug(true,true)
        }
        fun updateMoversBounds() {
            if (anim.frameSize>0) {
                anim.anchorOrder
                        .map { anim.anchors[it].anchorCoords[frameIndex] }
                        .forEachIndexed { i, anchorCoord -> partActors[i].setBounds(anchorCoord.x - 2 + root_x, anchorCoord.y - 2 + root_y, 4f, 4f) }
            }
        }
        override fun act(delta:Float) {
            super.act(delta)
            if (forEditing) updateMoversBounds()
        }

    }


    class FramesWindow(anim:Anim, skin:Skin, var viewUnits: Array<ViewUnit?>): Window("Frames",skin){

        var anim:Anim=anim
            set(anim){
                field=anim
                currentFrameIndex=0
                //previewTable.anim=anim
                previewGroup.anim=anim
                initializeFrameTables()
            }
        var currentFrameIndex:Int=0
            set(i){
                if (currentFrameIndex!=i) {
                    field=i
                    currentFrameIndexChanged=true
                    highlightSelectedFrame()
                }
            }
        var currentFrameIndexChanged=false
            get() = if (!field) false
            else { // when true
                field=false
                true
            }
        var framesTable=Table()
        var frameTableList= ArrayList<FrameTable>()
        //var previewTable :PreviewTable
        lateinit var previewGroup:FrameGroup

        init{
            initializeFrameModifyTable()
            initializeFrameTables()

            val frameScrollPane= ScrollPane(framesTable,skin)
            frameScrollPane.setFadeScrollBars(false)
            this.add(frameScrollPane).size(350f,120f).pad(3f).expand().fill()
            //previewTable=PreviewTable(anim,viewUnits,skin)
            previewGroup=FrameGroup(anim,viewUnits,false)
            val previewGroupTable= Table()
            this.add(previewGroupTable).size(96f).expand().fill()
            previewGroupTable.add(previewGroup).center()
        }

        fun initializeFrameModifyTable() {
            val frameModifyTable =Table(skin)
            val flipButton= TextButton("Flip",skin)
            flipButton.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float) {
                    flipAnimation()
                    true
                }
            })
            frameModifyTable.add(flipButton).pad(3f).colspan(2).grow().row()

            val addNextButton= TextButton("Add Next",skin)
            addNextButton.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float) {
                    addFrame(currentFrameIndex+1)
                    true
                }
            })
            frameModifyTable.add(addNextButton).pad(3f).colspan(2).grow().row()

            val addButton= TextButton("Add Last",skin)
            addButton.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float) {
                    addFrame()
                }
            })
            frameModifyTable.add(addButton).pad(3f).colspan(2).grow().row()

            val copyNextButton= TextButton("Copy Next",skin)
            copyNextButton.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float) {
                    copyFrame(currentFrameIndex,currentFrameIndex+1)
                    true

                }
            })
            frameModifyTable.add(copyNextButton).colspan(2).pad(3f).grow().row()

            val copyLastButton= TextButton("Copy Last",skin)
            copyLastButton.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float) {
                    copyFrame()
                    true

                }
            })
            frameModifyTable.add(copyLastButton).colspan(2).pad(3f).grow().row()
            val deleteButton= TextButton("Delete",skin)
            deleteButton.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float) {
                    deleteFrame(currentFrameIndex)
                    true
                }
            })
            frameModifyTable.add(deleteButton).colspan(2).pad(3f).grow().row()
            this.add(frameModifyTable).left().expand().fill().pad(3f)

            val moveLeftButton= TextButton("<",skin)
            moveLeftButton.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float) {
                    exchangeFrame(currentFrameIndex-1)
                    true

                }
            })
            frameModifyTable.add(moveLeftButton).pad(3f).grow()
            val moveRightButton= TextButton(">",skin)
            moveRightButton.addListener( object: ClickListener(){
                override fun clicked(e: InputEvent, x:Float, y:Float) {
                    exchangeFrame(currentFrameIndex)
                    true

                }
            })
            frameModifyTable.add(moveRightButton).pad(3f).grow().row()
        }
        fun initializeFrameTables() {
            frameTableList.clear()
            (0 .. anim.frameSize-1)
                    .map { FrameTable(this,anim, it,skin,viewUnits) }
                    .forEach { frameTableList.add(it) }
            validateFrameTables()

        }
        fun validateFrameTables() {
            framesTable.clearChildren()
            for (frame in frameTableList) {
                framesTable.add(frame).size(64f).pad(5f)
            }
        }
        fun highlightSelectedFrame() {
            for (frameTable in frameTableList) {
                frameTable.color = if (frameTable.frameIndex== currentFrameIndex) Color.RED
                                    else Color.WHITE

            }
        }
        fun deleteFrame(frameIndex:Int=anim.frameSize-1) {
            if (anim.deleteFrame(frameIndex)) {
                frameTableList.removeAt(frameTableList.size - 1)
                currentFrameIndex = 0
                validateFrameTables()
            }
        }
        fun addFrame(indexToAdd:Int=anim.frameSize) {
            if (anim.addFrame(indexToAdd)) {
                frameTableList.add(FrameTable(this,anim, anim.frameSize-1, skin, viewUnits))
                validateFrameTables()
            }
        }
        fun copyFrame(indexToCopy:Int=currentFrameIndex,indexToAdd:Int=anim.frameSize) {
            if( anim.copyFrame(indexToCopy,indexToAdd)) {
                frameTableList.add(FrameTable(this,anim, anim.frameSize-1, skin, viewUnits))
                validateFrameTables()
            }
        }
        fun exchangeFrame(index1:Int,index2:Int=index1+1) {
            anim.exchangeFrame(index1,index2)
        }
        fun flipAnimation() {
            anim.anchors.forEach{
                it.anchorCoords.forEach{
                    it.x=-it.x
                    it.dir= when (it.dir) {
                        Dir.DR ->Dir.DL
                        Dir.DL ->Dir.DR
                        Dir.UR ->Dir.UL
                        Dir.UL ->Dir.UR
                        else -> it.dir
                    }
                    it.rot=-it.rot
                }
            }
        }
        class FrameTable(framesWindow:FramesWindow, anim:Anim, frameIndex:Int, skin:Skin, viewUnits: Array<ViewUnit?>) :Button(skin){
            val label:Label= Label(frameIndex.toString(),skin)
            var frameIndex=frameIndex
                set (frameIndex:Int) {
                    field=frameIndex
                    label.setText(frameIndex.toString())
                }
            var anim=anim
            var viewUnits=viewUnits
            init{
                this.add(label).top().left().row()
                this.add(Table()).size(40f)
                this.addListener( object: ClickListener(){
                    override fun clicked(e: InputEvent, x:Float, y:Float) {
                        framesWindow.currentFrameIndex=frameIndex
                        true
                    }
                })
            }
            override fun draw(batch: Batch, delta:Float){
                super.draw(batch,delta)
                //viewUnit.draw(Dir.DL,batch,x+16f,y+16f,0f)
                anim.draw(batch,x+this.width*.5f,y+this.height*.5f-25f,frameIndex,viewUnits)
            }


        }
    }

    class AnchorsWindow(anim:Anim, skin:Skin, viewUnits: Array<ViewUnit?>): Window("Anchors",skin){

        var anim:Anim=anim
            set(anim:Anim){
                field=anim
                currentAnchorIndex=0
                //previewTable.anim=anim
                initializeAnchorTables()
            }
        var currentAnchorIndex:Int=0
            set(i){
                field=i
                highlightSelectedAnchor()
            }
        var currentFrameIndex:Int=0
        var viewUnits=viewUnits
        var anchorTables:HorizontalGroup = HorizontalGroup()
        var anchorTableList= ArrayList<AnchorTable>()
        var anchorModified=false
            get(){
                return if (field) {
                    field=false
                    true
                }
                else false
            }
        init {
            initializeAnchorTables()
            initializeAnchorModifyTable()
            val anchorScrollPane= ScrollPane(anchorTables,skin)
            anchorScrollPane.setFadeScrollBars(false)
            this.add(anchorScrollPane).height(156f).left().grow()
        }

        fun highlightSelectedAnchor() {
            for (i in anchorTableList.indices) {
                val anchorTable=anchorTableList[i]
         //       anchorTable.color = if (i== currentAnchorIndex) Color.RED
         //       else Color.WHITE
            }
        }
        fun initializeAnchorModifyTable() {
            val anchorModifyTable= Table(skin)
            val addButton = TextButton("Add Last", skin)
            addButton.addListener(object : ClickListener() {
                override fun clicked(e: InputEvent, x: Float, y: Float) {
                    addAnchor()
                }
            })
            anchorModifyTable.add(addButton).pad(3f).colspan(2).grow().row()
            val delButton = TextButton("Del Last", skin)
            delButton.addListener(object : ClickListener() {
                override fun clicked(e: InputEvent, x: Float, y: Float) {
                    deleteAnchor()
                }
            })
            anchorModifyTable.add(delButton).pad(3f).colspan(2).grow().row()

            this.add(anchorModifyTable).expand().left()
        }

        fun moveAnchorRight(anchorIndex: Int) {
            anim.moveAnchorRight(anchorIndex)
            this.validateAnchorTables()
        }
        fun moveAnchorLeft(anchorIndex: Int) {
            anim.moveAnchorLeft(anchorIndex)
            this.validateAnchorTables()
        }
        fun initializeAnchorTables() {
            anchorTableList.clear()
            for (anchor in anim.anchors){
                anchorTableList.add(AnchorTable(anchor,0,viewUnits,skin,this))
            }
            validateAnchorTables()
        }
        fun validateAnchorTables() {
            anchorTables.clearChildren()
            for (anchorIndex in anim.anchorOrder) {
                anchorTables.addActor(anchorTableList[anchorIndex])
            }
        }
        fun deleteAnchor() {
            if (anim.deleteAnchor()) {
                anchorTableList.removeAt(anchorTableList.size - 1)
                currentAnchorIndex = 0
                validateAnchorTables()
                anchorModified=true
            }
        }
        fun addAnchor() {
            anim.addAnchor()
            anchorTableList.add(AnchorTable(anim.anchors.get(anim.anchors.size-1),currentFrameIndex,viewUnits,skin,this))
            validateAnchorTables()
            anchorModified=true
        }

        fun setAnchorTables(frameIndex:Int) {
            currentFrameIndex=frameIndex
            for (anchorTable in anchorTables.children) {
                (anchorTable as AnchorTable).setFrameIndex(frameIndex)
            }

        }
        class AnchorTable(anchor: Anim.Anchor, frameIndex:Int, viewUnits: Array<ViewUnit?>, skin:Skin, anchorsWindow:AnchorsWindow) :Button(skin){
            val viewUnits=viewUnits
            val anchor: Anim.Anchor =anchor

            var anchorCoord:Anim.Anchor.AnchorCoord=anchor.anchorCoords[frameIndex]
            val label= Label("Parts: "+anchor.indices.toString(),skin)
            val xText :TextField =TextField((anchorCoord.x).toString(),skin)
            var xBackup: Float=anchorCoord.x
            val yText :TextField =TextField((anchorCoord.y).toString(),skin)
            var yBackup :Float=anchorCoord.y
            val rotText :TextField=TextField((anchorCoord.rot).toString(),skin)
            var rotBackup :Float=anchorCoord.rot
            val dirText :TextField=TextField((anchorCoord.dir).toString(),skin)
            var dirBackup :Dir=anchorCoord.dir

            val anchorNTextField:TextField=TextField("",skin)
            val spaceFinder: Regex=Regex("\\s+")
            init{
                this.addListener( object: ClickListener(){
                    override fun clicked(e: InputEvent, x:Float, y:Float) {
                        anchorsWindow.currentAnchorIndex=anchorsWindow.anchorTableList.indexOf(this@AnchorTable)
                        true
                    }
                })
                this.add(anchor.anchorN.toString()).top().left().pad(1f).row()
                val moveLeftButton=TextButton("<",skin)
                moveLeftButton.addListener( object: ClickListener(){
                    override fun clicked(e: InputEvent, x:Float, y:Float) {
                        anchorsWindow.moveAnchorLeft(anchor.anchorN)
                    }
                })
                val moveRightButton=TextButton(">",skin)
                moveRightButton.addListener( object: ClickListener(){
                    override fun clicked(e: InputEvent, x:Float, y:Float) {
                        anchorsWindow.moveAnchorRight(anchor.anchorN)
                    }
                })
                this.add(moveLeftButton).pad(1f).fill()
                this.add(moveRightButton).pad(1f).fill().row()
                this.add(label).colspan(2).left().pad(1f)
                this.add(anchorNTextField).colspan(2).pad(1f).width(70f).row()
                this.anchorNTextField.setTextFieldListener { textField, key
                    ->
                    if (key=='\r' ) {

                        try {
                            textField.getText()
                                    .trimStart().trimEnd()
                                    .split(spaceFinder)
                                    .map { it -> it.toInt() }
                                    .toTypedArray()
                        } catch (e: NumberFormatException){
                            textField.text = "ERR"
                            null
                        }?.let {
                            val indicesToChange = gdxArray<Int>(it)
                            if (indicesToChange.any {!(0<= it && it <viewUnits.size)}) {
                                textField.text="OOR"

                            }
                            else {
                                anchor.indices = indicesToChange
                                label.setText(anchor.indices.toString())
                            }
                        }
                    }

                }

                this.add("X:")
                this.add(xText).width(35f).pad(1f)
                this.add("Y:")
                this.add(yText).width(35f).pad(1f).row()
                this.add("ROT:")
                this.add(rotText).width(35f).pad(1f)
                this.add("DIR:")
                this.add(dirText).width(35f).pad(1f)

                this.xText.setTextFieldListener { textField, key
                    ->
                    if (key=='\r' ) {
                        try {
                            textField.text.toFloat()
                        }
                        catch (e:  NumberFormatException) {
                            textField.text="ERR"
                            null
                        }?.let {
                            anchorCoord.x = it
                        }
                    }

                }
                this.yText.setTextFieldListener { textField, key
                    ->
                    if (key=='\r' ) {
                        try {
                            textField.text.toFloat()
                        }
                        catch (e:  NumberFormatException) {
                            textField.text="ERR"
                            null
                        }?.let {
                            anchorCoord.y = it
                        }
                    }

                }
                this.rotText.setTextFieldListener { textField, key
                    ->
                    if (key=='\r' ) {
                        try {
                            textField.text.toFloat()
                        }
                        catch (e:  NumberFormatException) {
                            textField.text="ERR"
                            null
                        }?.let {
                            anchorCoord.rot = it
                        }
                    }

                }
                this.dirText.setTextFieldListener { textField, key
                    ->
                    if (key=='\r' ) {
                        try {
                            Dir.valueOf(textField.text)
                        }
                        catch (e:  IllegalArgumentException) {
                            textField.text="ERR"
                            null
                        }?.let {
                            anchorCoord.dir = it
                        }
                    }
                }

            }
            fun updateTexts() {
                if (xBackup!=anchorCoord.x) {
                    xBackup=anchorCoord.x
                    xText.setText((anchorCoord.x).toString())
                }
                if (yBackup!=anchorCoord.y) {
                    yBackup=anchorCoord.y
                    xText.setText((anchorCoord.y).toString())
                }
                if (rotBackup!=anchorCoord.rot) {
                    rotBackup=anchorCoord.rot
                    rotText.setText((anchorCoord.rot).toString())
                }
                if (dirBackup!=anchorCoord.dir) {
                    dirBackup=anchorCoord.dir
                    dirText.setText(dirBackup.toString())
                }
            }

            fun setFrameIndex(frameIndex: Int) {
                anchorCoord=anchor.anchorCoords[frameIndex]
                updateTexts()
            }

            override fun act(delta:Float) {
                super.act(delta)
                updateTexts()
            }
            override fun draw(batch:Batch,delta:Float) {
                super.draw(batch,delta)
                anchor.drawPart(batch,x+width*.75f,y+height*.75f,anchorCoord.dir,viewUnits)
            }
        }

    }

    class PreviewTable(anim:Anim,viewUnits: Array<ViewUnit?>,skin:Skin) :Table(skin){
        var anim:Anim=anim
        val viewUnits=viewUnits
        var time=0f
        override fun act(delta:Float){
            super.act(delta)
            time+=delta
        }
        override fun draw(batch:Batch, delta:Float) {
            super.draw(batch,delta)
            anim.draw(batch,x+width*.5f,y+height*.5f-25f,time,viewUnits)
        }

    }

    class ViewUnitActor(viewUnit:ViewUnit) : Table() {
        var viewUnit=viewUnit
        override fun act(delta :Float){
            super.act(delta)
            //this.setBounds(x,y,viewUnit.width,viewUnit.height)
        }
        override fun draw(batch:Batch,delta:Float){
            super.draw(batch,delta)
            viewUnit.draw(Dir.DL,batch,x+this.width*.5f,y+viewUnit.anchorY +this.height*.5f,0f)

        }

    }

}
