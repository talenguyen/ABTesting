package vn.tiki.ab;

/**
 * Created by KenZira on 11/24/16.
 */

public class Parser {

  private Parser() {
  }

  public static final ValueParser<Boolean> BOOLEAN = new ValueParser<Boolean>() {
    @Override public Boolean parse(String value) {
      return Boolean.valueOf(value);
    }
  };

  public static final ValueParser<Long> LONG = new ValueParser<Long>() {
    @Override public Long parse(String value) {
      return Long.valueOf(value);
    }
  };

  public static final ValueParser<Double> DOUBLE = new ValueParser<Double>() {
    @Override public Double parse(String value) {
      return Double.valueOf(value);
    }
  };

  public static final ValueParser<Float> FLOAT = new ValueParser<Float>() {
    @Override public Float parse(String value) {
      return Float.valueOf(value);
    }
  };

  public static final ValueParser<Byte> BYTE = new ValueParser<Byte>() {
    @Override public Byte parse(String value) {
      return Byte.valueOf(value);
    }
  };

  public static final ValueParser<Integer> INTEGER = new ValueParser<Integer>() {
    @Override public Integer parse(String value) {
      return Integer.valueOf(value);
    }
  };
}
