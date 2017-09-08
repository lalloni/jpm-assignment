README
======

FUNCTIONAL NOTES
----------------

* Requirement point `2.a.i` satisfied in `assignment.Stock::computeDividendYield`
* Requirement point `2.a.ii` satisfied in `assignment.Stock::computePriceEarningsRatio`
* Requirement point `2.a.iii` satisfied in `assignment.Stock::recordTrade`
* Requirement point `2.a.iv` satisfied in `assignment.Stock::computeFiveMinuteVolumeWeightedPriceValue`
* Requirement point `2.b` satisfied in `assignment.Exchange::computeFiveMinutesGBCEAllShareIndex`

DESIGN NOTES
------------

* Assumed GBCE All Share Index required in point `2.b` is to be based on the **last
  five minutes** Volume Weighted Stock Price as calculated in point `2.a.iv`
* Assumed multiple trades could be executed in the same exact `java.time.Instant`
* Used a `java.util.TreeMap` for storing trades indexed by `java.time.Instant` to quickly
  select the set of trades included in some time period (for volume weighted stock
  price over 5 minutes)

IMPLEMENTATION NOTES
--------------------

* Used `java.time.Instant` for all time stamp representation
* Used `java.math.BigDecimal` for all real number representation (in the real world
  should probably be replaced with `org.jscience.mathematics.number.Real` or
  `org.apfloat.Apfloat` for better performance)
* Used `long` for all integer number representation
* For simplicity (and to avoid reinventing the wheel) used Apfloat's arbitrary
  precision n-th root calculus implementation (based on Newton's method) for
  calculating the Geometric Mean used for the GBCE All Shared Index

USAGE
-----

Create an Exchange

```java
Exchange exchange = new Exchange();
```

Create a Stock and register it into the Exchange

```java
exchange.addStock(new Stock(Type.COMMON, new Symbol("FOO")));
```

Get the Stock from the Exchange

```java
Stock stock = exchange.getStock(Type.COMMON, new Symbol("FOO"));
```

Record trades into the Stock

```java
stock.recordTrade(new Trade(Instant.now(), Side.BUY, 200, new BigDecimal("100.50")))
stock.recordTrade(new Trade(Instant.now(), Side.SELL, 100, new BigDecimal("98.10")))
```

Set the stock last payed dividend, par value, fixed dividend rate (if appropriate)

```java
stock.setLastDividendValue(new BigDecimal("10.05"));
stock.setParValue(new BigDecimal("105.00"));
stock.setFixedDividendRate(new BigDecimal("0.04"));
```

Compute dividend yield and p/e ratio at a given price 

```java
BigDecimal price = new BigDecimal("100.50");
BigDecimal dividendYield = stock.computeDividendYield(price);
BigDecimal priceEarningsRatio = stock.computePriceEarningsRatio(price);
```

Compute the volume weighted price value using trades in the last five minutes

```java
BigDecimal fiveMinVolumeWeightedPrice = stock.computeFiveMinuteVolumeWeightedPrice();
```

Compute the volume weighted price value over using trades since a given time

```java
Instant since = Instant.now().minus(Duration.ofMinutes(15));
BigDecimal fifteenMinVolumeWeightedPrice = stock.computeVolumeWeightedPrice(since);
```

Compute the GBCE All Share Index using the last five minutes trades

```java
BigData fiveMinGBCEAllShareIndex = exchange.computeFiveMinutesGBCEAllShareIndex();
```

Compute the GBCE All Share Index using the trades since a given time

```java
Instant since = Instant.now().minus(Duration.ofMinutes(15));
BigData fifteenMinGBCEAllShareIndex = exchange.computeGBCEAllShareIndex(since);
```

