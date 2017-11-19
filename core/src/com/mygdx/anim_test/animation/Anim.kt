package com.mygdx.anim_test.animation

import com.badlogic.gdx.graphics.g2d.Batch
import java.util.*

/**
 * Created by Byungpil on 2017-06-15.
 */

//Add frame
//Add Anchor
// insert coord,rot,index..

typealias gdxArray<T> = com.badlogic.gdx.utils.Array<T>

class Anim (){
    internal val anchors = gdxArray<Anchor>()
                get() = field
    val anchorOrder= gdxArray<Int>()
    internal var frameSize=0
            get(){
                return field
            }
    private var animationDuration :Float =0f
    private var frameDuration :Float =0f
    internal var scaleX=1f
    internal var scaleY=1f

    init {
        frameDuration=1/8.0f

        addFrame()
        addAnchor(3)
        anchors[0].anchorCoords[0].set(-3f,1f,0f,Dir.DL)
        addAnchor(3)
        anchors[1].anchorCoords[0].set(3f,-1f,0f,Dir.DL)

        addAnchor(2,4)
        anchors[2].anchorCoords[0].set(-6f,6f,0f,Dir.DL)

        addAnchor(1)
        anchors[3].anchorCoords[0].set(0f,9f,0f,Dir.DL)

        addAnchor(0)
        anchors[4].anchorCoords[0].set(0f,15f,0f,Dir.DL)

        addAnchor(2,6)
        anchors[5].anchorCoords[0].set(6f,3f,0f,Dir.DL)

    }

    fun draw(batch : Batch,stateTime: Float,viewUnits :Array<ViewUnit?>){
        val frameIndex= getKeyFrameIndex(stateTime)
        draw(batch,frameIndex,viewUnits)
    }
    fun draw(batch : Batch,x:Float,y:Float,stateTime: Float,viewUnits :Array<ViewUnit?>){
        val frameIndex= getKeyFrameIndex(stateTime)
        if (frameIndex>=0) draw(batch,x,y,frameIndex,viewUnits)
    }
    fun draw(batch : Batch,frameIndex:Int,viewUnits :Array<ViewUnit?>){
        draw(batch,0f,0f,frameIndex,viewUnits)
    }
    fun draw(batch : Batch,x:Float,y:Float,frameIndex:Int,viewUnits :Array<ViewUnit?>){
        if (frameSize>0) {
            for (anchorIndex in anchorOrder) {
                val anchor = anchors[anchorIndex]
                for (viewUnitIndex in anchor.indices) {
                    val anchorCoord = anchor.anchorCoords[frameIndex]
                    viewUnits.getOrNull(viewUnitIndex)?.draw(anchorCoord.dir, batch, anchorCoord.x + x, anchorCoord.y + y, anchorCoord.rot)
                }

            }
        }
    }


    fun deleteFrame(frameIndex :Int=frameSize-1):Boolean {
        if (frameIndex in 0..frameSize-1) {
            anchors.forEach{it.anchorCoords.removeIndex(frameIndex)}
            frameSize--
            animationDuration -= frameDuration
            return true
        }
        else return false
    }
    fun addFrame(indexToAdd: Int=frameSize): Boolean {
        if (indexToAdd in 0..frameSize) {
            frameSize++
            animationDuration += frameDuration
            anchors.forEach{
                it.anchorCoords.insert(indexToAdd, Anchor.AnchorCoord(0f, 0f, 0f, Dir.DL))
            }
            return true
        }
        else return false
    }
    fun copyFrame(indexToCopy:Int, indexToAdd:Int=frameSize) :Boolean {
        if (indexToAdd in 0..frameSize && indexToCopy in 0..frameSize-1) {
            frameSize++
            animationDuration += frameDuration
            anchors.forEach{
                it.anchorCoords.insert(indexToAdd, it.anchorCoords[indexToCopy].copy())
            }
            return true
        }
        else return false
    }
    fun exchangeFrame(index1: Int , index2: Int=index1+1) {
        if (index1 in 0..(frameSize - 1) && index2 in 0..(frameSize-1))
            for (anchor in anchors) {
                val anchorCoordTemp= anchor.anchorCoords[index1]
                anchor.anchorCoords[index1]=anchor.anchorCoords[index2]
                anchor.anchorCoords[index2]=anchorCoordTemp
            }
    }

    fun moveAnchorRight(anchorN:Int){
        val index= this.anchorOrder.indexOf(anchorN)
        if (0<= index && index+1<this.anchorOrder.size) {
            val temp=this.anchorOrder[index]
            this.anchorOrder[index]=this.anchorOrder[index+1]
            this.anchorOrder[index+1]=temp
        }
    }
    fun moveAnchorLeft(anchorN :Int){
        val index= this.anchorOrder.indexOf(anchorN)
        if (1<= index && index<this.anchorOrder.size) {
            val temp=this.anchorOrder[index]
            this.anchorOrder[index]=this.anchorOrder[index-1]
            this.anchorOrder[index-1]=temp
        }
    }

    fun addAnchor(vararg viewUnitNs:Int) {
        anchors.add(Anchor(anchors.size,viewUnitNs,frameSize))
        anchorOrder.add(anchors.size-1)
    }
    fun addAnchor() {
        anchors.add(Anchor(anchors.size,null,frameSize))
        anchorOrder.add(anchors.size-1)
    }

    fun deleteAnchor() :Boolean{
        when{
            anchors.size>0 -> {
                anchors.removeIndex(anchors.size - 1)
                anchorOrder.removeIndex(anchors.size)
                return true
            }
            else -> {
                return false
            }
        }

    }


    fun getKeyFrameIndex (stateTime :Float) : Int {
        if (frameSize==0 || frameDuration==0f) return -1
        //if (frameSize<=1 || frameDuration==0f) return 0
        var frameNumber = (stateTime/frameDuration).toInt()
        frameNumber %= frameSize
        return frameNumber
    }

    class Anchor() {
        var anchorN :Int
        var indices= gdxArray<Int>()
        val anchorCoords=gdxArray<AnchorCoord>()
        init {
            this.anchorN=-1
        }
        constructor(anchorN:Int,viewUnitNs: IntArray?,AnchorCoordN: Int) : this() {
            this.anchorN=anchorN
            for (i in 0..AnchorCoordN-1) {
                anchorCoords.add(AnchorCoord())
            }
            viewUnitNs?.let {
                for (i in 0..viewUnitNs.size - 1) {
                    indices.add(viewUnitNs[i])
                }
            }
        }
        fun drawPart(batch: Batch, x:Float, y:Float, dir: Dir, viewUnits: Array<ViewUnit?>) {
            for (viewUnitIndex in this.indices){
                viewUnits[viewUnitIndex]?.draw(dir,batch,x,y,0f)
            }
        }

        class AnchorCoord(x:Float=0f,y:Float=0f,rot:Float=0f,dir:Dir=Dir.DL)
        {

            var x:Float=x
            var y:Float=y
            var rot:Float=rot
            var dir:Dir=dir
            var isVisible = ArrayList<Boolean>()
            init {

            }
            fun set(x:Float,y:Float,rot:Float,dir:Dir) {
                this.x=x
                this.y=y
                this.rot=rot
                this.dir=dir
            }

            fun copy():AnchorCoord {
                val toReturn =AnchorCoord(x,y,rot,dir)
                toReturn.isVisible=this.isVisible.clone() as ArrayList<Boolean>
                return toReturn
            }

        }
    }



}

