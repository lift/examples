/*
 * Copyright 2008-2010 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.liftweb {
package jpademo {
package model {

import _root_.java.util.Currency
import _root_.java.util.Locale
import _root_.java.text.NumberFormat

/* currency factory*/
abstract class CurrencyZone {
  type Currency <: AbstractCurrency

  def make(x: BigDecimal): Currency

  abstract class AbstractCurrency {
    val amount: BigDecimal
    val designation: String
    val currencySymbol: String
    val numberOfFractionDigits: Int
    val auLocale: Locale
    val scale: Int

    def +(that: Currency): Currency = this + that

    def *(that: Currency): Currency = this * that

    def -(that: Currency): Currency = this - that

    def /(that: Currency): Currency =
    make(new BigDecimal(this.amount.bigDecimal.divide(that.amount.bigDecimal, scale, _root_.java.math.BigDecimal.ROUND_HALF_UP)) )

    override def toString = format("", numberOfFractionDigits)

    def format: String = format(currencySymbol, numberOfFractionDigits)

    def format(currencySymbol: String, numberOfFractionDigits: Int): String = {
      var moneyValue = amount
      if (amount == null) moneyValue = 0

      moneyValue = moneyValue.setScale(numberOfFractionDigits, BigDecimal.RoundingMode.HALF_UP);
      val numberFormat = NumberFormat.getInstance(auLocale);
      numberFormat.setMinimumFractionDigits(numberOfFractionDigits);
      numberFormat.setMaximumFractionDigits(numberOfFractionDigits);
      if (moneyValue.doubleValue() < 0) return "-"+currencySymbol+numberFormat.format(moneyValue.abs.doubleValue());
      else return currencySymbol+numberFormat.format(moneyValue.doubleValue());
    }

    def get: String = get(numberOfFractionDigits)

    def get(numberOfFractionDigits: Int): String = format("", numberOfFractionDigits).replaceAll(",", "");

  }
  val CurrencyUnit: Currency
}

}
}
}

