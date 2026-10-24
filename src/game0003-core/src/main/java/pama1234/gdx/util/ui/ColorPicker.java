package pama1234.gdx.util.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class ColorPicker extends Dialog{
  private static final Skin defaultSkin=new Skin(Gdx.files.internal("gdx/uiskin.json"));

  private final Color selectedColor;
  private final ColorPickerListener listener;
  private Slider redSlider;
  private Slider greenSlider;
  private Slider blueSlider;

  public ColorPicker(ColorPickerListener listener) {
    super("选择颜色",defaultSkin);
    this.selectedColor=new Color(Color.WHITE);
    this.listener=listener;
    init();
  }

  public void init() {
    Table contentTable=getContentTable();
    contentTable.align(Align.center);

    redSlider=createColorSlider("Red",Color.RED);
    greenSlider=createColorSlider("Green",Color.GREEN);
    blueSlider=createColorSlider("Blue",Color.BLUE);

    contentTable.add(redSlider).row();
    contentTable.add(greenSlider).row();
    contentTable.add(blueSlider).row();

    TextButton okButton=new TextButton("确定",getSkin());
    okButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event,float x,float y) {
        listener.colorSelected(selectedColor);
        hide();
      }
    });

    TextButton cancelButton=new TextButton("取消",getSkin());
    cancelButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event,float x,float y) {
        hide();
      }
    });

    button(okButton);
    button(cancelButton);
  }

  private Slider createColorSlider(String label,Color color) {
    Slider slider=new Slider(0,1,0.01f,false,getSkin());
    slider.addListener(new ClickListener() {
      @Override
      public void touchDragged(InputEvent event,float x,float y,int pointer) {
        updateSelectedColor();
        super.touchDragged(event,x,y,pointer);
      }

      @Override
      public boolean touchDown(InputEvent event,float x,float y,int pointer,int button) {
        updateSelectedColor();
        return super.touchDown(event,x,y,pointer,button);
      }
    });
    return slider;
  }

  private void updateSelectedColor() {
    selectedColor.set(redSlider.getValue(),greenSlider.getValue(),blueSlider.getValue(),1);
  }

  @Override
  public Dialog show(Stage stage) {
    var o=super.show(stage);
    setPosition(stage.getWidth()/2-getWidth()/2,stage.getHeight()/2-getHeight()/2);
    return o;
  }

  public interface ColorPickerListener{
    void colorSelected(Color color);
  }
}
