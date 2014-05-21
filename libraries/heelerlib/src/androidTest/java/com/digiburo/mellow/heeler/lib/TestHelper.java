package com.digiburo.mellow.heeler.lib;

import java.util.Random;

/**
 * @author gsc
 */
public class TestHelper {
  private Random random = new Random();

  public Double randomDouble() {
    return(random.nextDouble());
  }

  public Float randomFloat() {
    return(random.nextFloat());
  }

  public Integer randomInteger() {
    return(random.nextInt());
  }

  public Long randomLong() {
    return(random.nextLong());
  }

  public String randomString() {
    return(Long.toHexString(random.nextLong()));
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 19, 2014 by gsc
 */
