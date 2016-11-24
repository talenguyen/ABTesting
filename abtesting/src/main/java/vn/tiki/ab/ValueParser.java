package vn.tiki.ab;

/**
 * Created by KenZira on 11/24/16.
 */

interface ValueParser<T> {
  T parse(String value);
}
