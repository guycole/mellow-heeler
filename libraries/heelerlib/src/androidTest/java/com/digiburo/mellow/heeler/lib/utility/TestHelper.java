package com.digiburo.mellow.heeler.lib.utility;

import java.util.Random;

/**
 * @author gsc
 */
public class TestHelper {

  public Double randomDouble() {
    return(_random.nextDouble());
  }

  public Integer randomInteger() {
    return(_random.nextInt());
  }

  public String randomString() {
    return(Long.toHexString(_random.nextLong()));
  }

  Random _random = new Random();
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 19, 2014 by gsc
 */
