package pama1234.gdx.game.duel.util.graphics;

import com.badlogic.gdx.graphics.Texture;

import pama1234.gdx.game.duel.Duel;
import pama1234.gdx.util.element.Graphics;
import pama1234.gdx.util.entity.Entity;
import pama1234.math.UtilMath;

public class DemoInfo extends Entity<Duel>{
  public Graphics text;
  public DemoInfo(Duel p) {
    super(p);
    refreshGraphics(p);
  }

  @Override
  public void frameResized(int w, int h) {
    refreshGraphics(p);
  }

  public void refreshGraphics(Duel p) {
    int fu=UtilMath.min(p.width,p.height);
    int tf=UtilMath.max(1,(int)(fu/460));

    text=new Graphics(p,576*tf,460*tf);
    text.begin();
    p.beginShape();
    p.beginBlend();
    //    p.background(p.theme().background,200);
    p.doStroke();
    p.stroke(p.theme().stroke);
    int strokeWeight=2;
    p.strokeWeightOriginal(strokeWeight);
    //    p.noFill();
    p.fill(p.theme().background,200);
    p.rect(strokeWeight,strokeWeight,text.texture.getWidth()-strokeWeight,text.texture.getHeight()-strokeWeight*2);
    p.endBlend();
    p.endShape();
    p.pushMatrix();
    p.scale(tf);

    p.textScale(1);
    p.strokeWeightOriginal(1);
    p.textColor(p.theme().text);
    if(p.isAndroid) drawText_ch_android(p,UtilMath.min(p.width,p.height));
    else drawText_ch(p,UtilMath.min(p.width,p.height));
    p.popMatrix();
    text.end();
  }

  public static void displayDemo(Duel p) {
    int fu=UtilMath.min(p.width,p.height);
    Texture img=p.demoInfo.text.texture;
    int tf=UtilMath.max(1,(int)(fu/img.getHeight()));
    int tw=img.getWidth()*tf;
    int th=img.getHeight()*tf;
    p.image(img,(p.width-tw)/2f,(p.height-th)/2f,tw,th);
  }
  public static void drawText_en(Duel p,int fu) {
    p.text("    Z key:",200,180);
    p.text("    X key:",200,250);
    p.text("Arrow key:",200,345);
    p.text("Weak shot\n (auto aiming)",300,180);
    p.text("Lethal shot\n (manual aiming,\n  requires charge)",300,250);
    p.text("Move\n (or aim lethal shot)",300,345);
    p.text("- Press Z key to start -",192,430);
    p.text("(Click to hide this window)",192,475);
  }
  public static void drawText_ch(Duel p,int fu) {
    p.textScale(3);
    p.text("几何决斗！",180,20);
    p.textScale(1);
    p.text("      Z 按键:",180,100);
    p.text("      X 按键:",180,170);
    p.text("左手触摸屏幕:",180,245);
    p.text("普通攻击\n (自动瞄准)",300,100);
    p.text("致命大招\n (手动瞄准,\n  需要蓄力)",300,170);
    p.text("移动\n (或使用大招时进行瞄准)",300,245);
    p.text("- 按 Z 键开始游戏 -",192,330);
    p.text("(轻触显示或隐藏此界面)",192,360);
    p.textColor(p.theme().text,192);
    p.text("由FAL制作！( https://www.fal-works.com/ )",20,400);
    p.text("由Pama1234移植到安卓版！( https://space.bilibili.com/646050693 )",20,420);
    p.text("原型版本，视觉BUG很多，敬请关注此开源项目！会更新联机版！",20,440);
  }
  public static void drawText_ch_android(Duel p,int fu) {
    p.textScale(3);
    p.text("几何决斗！",180,20);
    p.textScale(2);
    p.text("      Z 按键:",60,100);
    p.text("      X 按键:",60,140);
    p.text("左手触摸屏幕:",60,190);

    p.text("普通攻击",300,100);
    p.text("致命大招",300,140);
    p.text("移动或瞄准",300,190);

    p.text("- 按 Z 键开始游戏 -",160,300);
    p.text("(轻触显示或隐藏此界面)",160,340);
    p.textColor(p.theme().text,192);
    p.textScale(1);
    p.text("由FAL制作！( https://www.fal-works.com/ )",20,380);
    p.text("由Pama1234移植到安卓版！( https://space.bilibili.com/646050693 )",20,400);
    p.text("敬请关注此开源项目！会更新联机版！",20,420);
  }
}