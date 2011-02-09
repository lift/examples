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

import _root_.java.util.Currency;
import _root_.java.util.Locale;
import _root_.java.text.NumberFormat;

/* Australian Money */
object AU extends CurrencyZone {
  type Currency = AUD

  abstract class AUD extends AbstractCurrency {
    override val designation = "AUD"
    override val numberOfFractionDigits = 2
    override val currencySymbol = "$"
    override val auLocale = new Locale("en", "AU")
    override val scale = 10
  }

  def make(x: BigDecimal) = new AUD {
    val amount = x
  }

  def apply(x: BigDecimal): AUD = make(x)
  def apply(x: String): AUD = make(BigDecimal(x))
  def apply(): AUD = make(BigDecimal(0))

  val Cent = make(.01)
  val Dollar = make(1)
  val CurrencyUnit = Dollar
}
}
}
}

