
package edu.vt.controllers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named(value = "sliderController")
@RequestScoped
public class SliderController {
  private List<String> sliderImages;

  @PostConstruct
  public void init() {
    sliderImages = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      sliderImages.add("photo" + i + ".png");
    }
  }
  
  public List<String> getSliderImages() {
    return sliderImages;
  }

  /**
   * Get the description of an image based on its filename.
   * @param image The filename of the image being displayed.
   * @return A string for the description of the given image.
   */
  public String description(String image) {
    String imageDescription = "";
    switch (image) {
      case "photo1.png":
        imageDescription = "";
        break;
      case "photo2.png":
        imageDescription = "";
        break;
      case "photo3.png":
        imageDescription = "";
        break;
      case "photo4.png":
        imageDescription = "";
        break;
      case "photo5.png":
        imageDescription = "";
        break;
      case "photo6.png":
        imageDescription = "";
        break;
      case "photo7.png":
        imageDescription = "";
        break;
      case "photo8.png":
        imageDescription = "";
        break;
      case "photo9.png":
        imageDescription = "";
        break;
      case "photo10.png":
        imageDescription = "";
        break;
      case "photo11.png":
        imageDescription = "";
        break;
      case "photo12.png":
        imageDescription = "";
        break;
    }
    return imageDescription;
  }
}
