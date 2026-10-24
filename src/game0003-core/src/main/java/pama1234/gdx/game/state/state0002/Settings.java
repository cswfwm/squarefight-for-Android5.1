package pama1234.gdx.game.state.state0002;

import static pama1234.gdx.game.duel.util.input.UiGenerator.*;

import pama1234.app.game.server.duel.ServerConfigData.ServerAttr;
import pama1234.gdx.MobileUtil;
import pama1234.gdx.Pama;
import pama1234.gdx.game.duel.Duel;
import pama1234.gdx.game.duel.State0002Util.StateEntity0002;
import pama1234.gdx.game.duel.util.theme.ThemeData;
import pama1234.gdx.game.ui.element.Slider;
import pama1234.gdx.game.ui.element.TextButton;
import pama1234.gdx.game.ui.element.TextButtonCam;
import pama1234.gdx.game.ui.element.TextField;
import pama1234.gdx.launcher.MainApp;
import pama1234.gdx.util.android.AndroidCtrlBase;
import pama1234.gdx.util.cam.CameraController2D;
import pama1234.gdx.util.ui.ColorPicker;
import pama1234.gdx.util.ui.editor.TextEditor;

public class Settings extends StateEntity0002{
  public TextEditor<?>[] textEditors;
  public TextButton<?>[] buttons;
  public TextButtonCam<?>[] camButtons;

  public Settings(Duel p,int id) {
    super(p,id);
    init();
  }

  @Override
  public void init() {
    textEditors=generateTextEditors(p);
    buttons=genReturnButton(p);
    camButtons=generateCamButtons(p);
  }

  @Override
  public void from(StateEntity0002 in) {
    configureCameraForSettings();
    addUiElements();
  }

  @Override
  public void to(StateEntity0002 in) {
    resetCameraConfiguration();
    removeUiElements();
  }

  private void configureCameraForSettings() {
    p.camStrokeWeight=()->p.cam2d.pixelPerfect==CameraController2D.SMOOTH?p.cam2d.scale.pos:p.u/16*p.cam2d.scale.pos;
    p.cam2d.pixelPerfect=CameraController2D.SMOOTH;
    p.cam2d.scale.des=2;
    p.cam2d.point.des.y=40;
  }

  private void resetCameraConfiguration() {
    p.camStrokeWeight=()->p.u/16*p.cam2d.scale.pos;
    p.cam2d.pixelPerfect=CameraController2D.NONE;
  }

  private void addUiElements() {
    for(TextEditor<?> editor:textEditors) {
      p.centerCam.add.add(editor);
      editor.addTo(p.camStage);
    }
    for(TextButton<?> button:buttons) {
      p.centerScreen.add.add(button);
    }
    for(TextButtonCam<?> camButton:camButtons) {
      p.centerCam.add.add(camButton);
    }
  }

  private void removeUiElements() {
    for(TextEditor<?> editor:textEditors) {
      p.centerCam.remove.add(editor);
      editor.removeFrom(p.camStage);
    }
    for(TextButton<?> button:buttons) {
      p.centerScreen.remove.add(button);
    }
    for(TextButtonCam<?> camButton:camButtons) {
      p.centerCam.remove.add(camButton);
    }
  }

  private static TextEditor<?>[] generateTextEditors(Duel p) {
    TextEditor<?>[] editors=new TextEditor[] {
      createSkinSettingsEditor(p),
      createServerSettingsEditor(p)
    };
    return p.debug?editors:new TextEditor[] {editors[0]};
  }

  private static TextButtonCam<?>[] generateThemeButtons(Duel p) {
    return new TextButtonCam[] {
      new TextButtonCam<>(p,self->self.text="背景颜色",()->true,true)
        .allTextButtonEvent(self-> {},self-> {},self-> {
          ColorPicker colorPicker=new ColorPicker(selectedColor-> {
            p.theme().background=selectedColor;
          });
          colorPicker.setPosition(p.mouse.x,p.mouse.y);
          colorPicker.show(p.camStage);
        })
        .rectAuto(()->120,()->-250),
      new TextButtonCam<>(p,self->self.text="文本颜色",()->true,true)
        .allTextButtonEvent(self-> {},self-> {},self-> {
          ColorPicker colorPicker=new ColorPicker(selectedColor-> {
            p.theme().text=selectedColor;
          });
          colorPicker.setPosition(p.mouse.x,p.mouse.y);
          colorPicker.show(p.camStage);
        })
        .rectAuto(()->120,()->-230),
      // Add more buttons for other settings as needed
    };
  }

  private static TextEditor<?> createSkinSettingsEditor(Duel p) {
    return new TextEditor<>(p,p.theme().stroke,-160,-160,320,480) {
      @Override
      public void display() {
        super.display();
        p.textColor(p.theme().text);
        p.text("皮肤设置",rect.x(),rect.y()-20);
      }

      @Override
      public void keyboardHidden(TextField in) {
        if(in==textArea) {
          try {
            p.config.customThemeData.data=Duel.localization.yaml.load(textArea.getText());
            p.theme(ThemeData.fromData(p.config.customThemeData));
          }catch(RuntimeException e) {
            textArea.setText(getSkinText(p));
          }
        }
      }
    }.setTextAreaProperties(getSkinText(p),"皮肤设置");
  }

  private static TextEditor<?> createServerSettingsEditor(Duel p) {
    return new TextEditor<>(p,p.theme().stroke,180,-160,320,60) {
      @Override
      public void keyboardHidden(TextField in) {
        if(in==textArea) {
          p.config.data.server=Duel.localization.yaml.loadAs(in.getText(),ServerAttr.class);
        }
      }
    }.setTextAreaProperties(getServerAttrText(p),"联机设置");
  }

  private static TextButtonCam<?>[] generateCamButtons(Duel p) {
    TextButtonCam[] themeButtons=generateThemeButtons(p);
    TextButtonCam[] otherButtons=new TextButtonCam[] {
      createRestartButton(p),
      createGameModeButton(p),
      createOrientationButton(p),
      createThemeButton(p),
      createDifficultyButton(p),
      createSparringButton(p),
      createVolumeSlider(p),
      createFpsFixButton(p),
    };

    TextButtonCam<?>[] allButtons=new TextButtonCam[themeButtons.length+otherButtons.length];
    System.arraycopy(themeButtons,0,allButtons,0,themeButtons.length);
    System.arraycopy(otherButtons,0,allButtons,themeButtons.length,otherButtons.length);

    return allButtons;
  }

  private static TextButtonCam<?> createRestartButton(Duel p) {
    return new TextButtonCam<>(p,self->self.text="重启游戏")
      .allTextButtonEvent(self-> {},self-> {},self->MainApp.instance.restartScreen())
      .rectAuto(()->-40,()->-250);
  }

  private static TextButtonCam<?> createGameModeButton(Duel p) {
    return new TextButtonCam<>(p,self->self.text="模式："+p.config.data.gameMode.toString(),()->p.debug,true)
      .allTextButtonEvent(self-> {},self-> {},self-> {
        p.config.data.gameMode=p.config.data.gameMode.next();
        self.updateText();
      })
      .rectAuto(()->-40,()->-230);
  }

  private static TextButtonCam<?> createOrientationButton(Duel p) {
    return new TextButtonCam<>(p,self->self.text="显示方向："+(p.config.data.orientation==MobileUtil.landscape?"横向":"竖向"))
      .allTextButtonEvent(self-> {},self-> {},self-> {
        p.config.data.orientation=(p.config.data.orientation+1)%2;
        if(p.isAndroid) {
          if(p.config.data.orientation==1) {
            p.stateCenter.game.actrl.activeCondition=AndroidCtrlBase.portraitCondition;
          }else {
            p.stateCenter.game.actrl.activeCondition=AndroidCtrlBase.landscapeCondition;
          }
          Pama.mobile.orientation(p.config.data.orientation);
        }
        self.updateText();
      })
      .rectAuto(()->-40,()->-210);
  }

  private static TextButtonCam<?> createThemeButton(Duel p) {
    return new TextButtonCam<>(p,self->self.text="主题："+p.config.data.themeType.toString())
      .allTextButtonEvent(self-> {},self-> {},self-> {
        p.config.data.themeType=p.config.data.nextTheme(p.config.data.themeType);
        p.config.updateThemeFromType(p.config.data.themeType);
        self.updateText();
      })
      .rectAuto(()->-40,()->-190);
  }

  private static String difficultyName(float d) {
    if(d<0.7f) return "小";
    if(d<1.5f) return "中";
    return "大";
  }

  private static TextButtonCam<?> createDifficultyButton(Duel p) {
    return new TextButtonCam<>(p,self->self.text="AI难度："+difficultyName(p.config.data.difficulty))
      .allTextButtonEvent(self-> {},self-> {},self-> {
        float d=p.config.data.difficulty;
        if(d<0.7f) p.config.data.difficulty=1.2f;
        else if(d<1.5f) p.config.data.difficulty=1.8f;
        else p.config.data.difficulty=0.4f;
        self.updateText();
      })
      .rectAuto(()->-40,()->-170);
  }

  private static TextButtonCam<?> createSparringButton(Duel p) {
    return new TextButtonCam<>(p,self->self.text="陪练AI："+(p.config.data.sparring?"开":"关"))
      .allTextButtonEvent(self-> {},self-> {},self-> {
        p.config.data.sparring=!p.config.data.sparring;
        self.updateText();
      })
      .rectAuto(()->-40,()->-150);
  }

  private static Slider<?> createVolumeSlider(Duel p) {
    return new Slider<>(p,true,()->true,self-> {
      p.config.data.volume=self.pos;
      self.updateText();
    },self-> {},self-> {},self->self.text="音量："+String.format("%6.2f",p.config.data.volume*100),()->18,()->-40,()->-270,1)
      .pos(p.config.data.volume);
  }

  private static TextButtonCam<?> createFpsFixButton(Duel p) {
    return new TextButtonCam<>(p,self->self.text="FPS修复：（需重启游戏）"+(p.config.data.fpsFix?"是":"否"))
      .allTextButtonEvent(self-> {},self-> {},self-> {
        p.config.data.fpsFix=!p.config.data.fpsFix;
        self.updateText();
      })
      .rectAuto(()->-40,()->-290);
  }
}
