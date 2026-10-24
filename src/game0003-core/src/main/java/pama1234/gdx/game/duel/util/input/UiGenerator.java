package pama1234.gdx.game.duel.util.input;

import com.badlogic.gdx.Input;

import pama1234.Tools;
import pama1234.gdx.game.duel.Duel;
import pama1234.gdx.game.ui.element.TextButton;
import pama1234.gdx.util.ui.UiGeneratorBase;

public class UiGenerator extends UiGeneratorBase{
  public static TextButton<?>[] genButtons_0001(Duel p) {
    return new TextButton[] {
      new TextButton<>(p,true,()->true,self-> {},self-> {
        p.inputProcessor.keyDown(Input.Keys.Z);
      },self-> {
        p.inputProcessor.keyUp(Input.Keys.Z);
      },self->self.text="Z ",p::getButtonUnitLength,()->p.width-p.bu*4f,()->p.height-p.bu*1.5f,()->p.bu-p.pus).mouseLimit(false),
      new TextButton<>(p,true,()->true,self-> {},self-> {
        p.inputProcessor.keyDown(Input.Keys.X);
      },self-> {
        p.inputProcessor.keyUp(Input.Keys.X);
      },self->self.text=" X",p::getButtonUnitLength,()->p.width-p.bu*2.5f,()->p.height-p.bu*1.5f,()->p.bu-p.pus).mouseLimit(false),
    };
  }
  @Deprecated
  public static TextButton<?>[] genButtons_0002(Duel p) {
    return new TextButton[] {
      new TextButton<>(p,true,()->true,self-> {},self-> {
        if(p.state==p.stateCenter.game) p.state(p.stateCenter.settings);
        else p.state(p.stateCenter.game);
        self.updateText();
      },self-> {},self->self.text=p.state==p.stateCenter.game?"设置":"游戏",p::getButtonUnitLength,()->p.width-p.bu*2.5f,()->p.bu*0.5f,()->p.bu-p.pus).mouseLimit(false),
    };
  }
  public static String getSkinText(Duel p) {
    return p.config.customThemeData==null?"无可加载的皮肤配置，重启游戏试试":Duel.localization.yaml.dumpAsMap(p.config.customThemeData.data);
  }
  public static String getServerAttrText(Duel p) {
    return p.config.data.server==null?"无可加载的联机配置，重启游戏试试":Duel.localization.yaml.dumpAsMap(p.config.data.server);
  }
  public static TextButton<?>[] genButtons_0004(Duel p) {
    // TODO 重写按钮逻辑
    var out=new TextButton[] {
      new TextButton<>(p,true,()->true,self-> {},self-> {},self-> {
        // if(p.debug) {
        //   // System.out.println("UiGenerator.genButtons_0004() firstPlay "+p.config.data.firstPlay);
        //   // p.config.data.firstPlay=true;
        //   p.state(p.stateCenter.debug.gamePrototype);
        //   return;
        // }
        if(p.config.data.firstPlay) {
          // p.config.data.firstPlay=false;
          p.state(p.stateCenter.tutorial);
        }else p.state(p.stateCenter.game);
      },self->self.text="开始游戏",()->p.bu,()->(int)((p.width-p.textWidth("开始游戏"))/2f-p.pu/2),()->(int)(p.height*0.45f)),
      new TextButton<>(p,true,()->true,self-> {},self-> {},self-> {
        p.state(p.stateCenter.settings);
      },self->self.text="  设置  ",()->p.bu,()->(int)((p.width-p.textWidth("  设置  "))/2f-p.pu/2),()->(int)(p.height*0.45f+p.bu*1.2f)),
    };
    if(p.debug) {
      out=Tools.concat(out,new TextButton[] {
        new TextButton<>(p,true,()->true,self-> {},self-> {},self-> {
          p.state(p.stateCenter.debug.gamePrototype);
        },self->self.text="原型测试",()->p.bu,()->(int)((p.width-p.textWidth("原型测试"))/2f-p.pu/2),()->(int)(p.height*0.45f+p.bu*2.4f)),});
    }
    return out;
  }
  /**
   * 生成返回按钮
   * 
   * @param p
   * @return
   */
  public static TextButton<?>[] genReturnButton(Duel p) {
    return new TextButton[] {
      new TextButton<>(p,self->self.text="返回",()->true,true).allTextButtonEvent(self-> {},self-> {},self-> {
        p.state(p.stateCenter.startMenu);
      }).rectAutoWidth(()->(int)(p.width-p.bu*2.5f),()->(int)(p.bu*0.5f),()->p.bu-p.pus).mouseLimit(true),
    };
  }
}