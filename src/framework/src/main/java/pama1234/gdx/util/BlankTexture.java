package pama1234.gdx.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * 生成 1x1 白色空白纹理区域，用于纯色矩形绘制。
 * 公开版 shapedrawer 未提供 ShapeDrawer.createBlankTextureRegion()，此处补齐等价实现。
 */
public final class BlankTexture{
  private static TextureRegion region;
  public static TextureRegion createBlankTextureRegion() {
    if(region==null) {
      Pixmap pixmap=new Pixmap(1,1,Pixmap.Format.RGBA8888);
      pixmap.setColor(Color.WHITE);
      pixmap.fill();
      Texture texture=new Texture(pixmap);
      pixmap.dispose();
      region=new TextureRegion(texture);
    }
    return region;
  }
}