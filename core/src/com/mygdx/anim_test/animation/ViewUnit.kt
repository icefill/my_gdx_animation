package com.mygdx.anim_test.animation

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Created by Byungpil on 2017-06-15.
 */

enum class Dir(val v: Int) {
    DL(0),
    DR(1),
    UR(2),
    UL(3),
    AB(4),
    BL(5)
}

class ViewUnit (size:Float, anchor_x:Float,anchor_y:Float,dl: TextureRegion, ur: TextureRegion? =null ,dr: TextureRegion? =null, ul: TextureRegion?= null){
    private val regions = ArrayList<TextureRegion?>(4)
    internal val anchorX:Float
    internal val anchorY:Float
    internal val width :Float=size
    internal val height :Float=size
    internal var rotCenterX:Float=anchor_x
    internal var rotCenterY:Float=anchor_y



    private val has_direction :Boolean =true
    init {
        regions.add(null)
        regions.add(null)
        regions.add(null)
        regions.add(null)

        regions[Dir.DL.v] = dl
        this.anchorX =anchor_x
        this.anchorY =anchor_y
        this.rotCenterX =anchor_x
        this.rotCenterY =anchor_y
        if (ur!=null) {
            regions[Dir.UR.v]=ur
            if (dr==null) {
                regions[Dir.DR.v]=TextureRegion(regions[Dir.DL.v]);regions[Dir.DR.v]?.flip(true,false)
                regions[Dir.UL.v]=TextureRegion(regions[Dir.UR.v]);regions[Dir.UL.v]?.flip(true,false)
            }
            else {
                regions[Dir.DR.v]=dr
                regions[Dir.DL.v]=ul
            }
        }
        else {
            regions[3]=dl
            regions[2]=dl
            regions[1]=dl
        }

    }
    fun draw(dir: Dir, batch :Batch, x: Float, y:Float, rot: Float) {
        batch.draw(regions[dir.v],x- anchorX,y- anchorY, rotCenterX, rotCenterY,width,height, 1f, 1f,rot)
    }
    fun draw(dir: Dir, batch :Batch, x: Float, y:Float,scaleX:Float,scaleY:Float, rot: Float) {
        batch.draw(regions[dir.v],x- anchorX*scaleX,y- anchorY*scaleY, rotCenterX, rotCenterY,width,height, scaleX, scaleY,rot)
    }
    fun getRegion(n: Int) = regions[n]


}
