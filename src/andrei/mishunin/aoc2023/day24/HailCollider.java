package andrei.mishunin.aoc2023.day24;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class HailCollider {
    Line3D[] lines;

    public HailCollider(String fileName) {
        lines = InputReader.readAllLines(fileName).stream()
                .filter(s -> !s.isBlank())
                .map(Line3D::new)
                .toArray(Line3D[]::new);
    }

    public int countCollides2D(BigDecimal minArea, BigDecimal maxArea) {
        int crossedCount = 0;
        for (int i = 0; i < lines.length - 1; i++) {
            Line3D l1 = lines[i];
            for (int j = i + 1; j < lines.length; j++) {
                Line3D l2 = lines[j];
                Point2D crossed = cross2D(
                        new Line2D(
                                new Point2D(l1.start.x, l1.start.y),
                                new Point2D(l1.velocity.x, l1.velocity.y)
                        ),
                        new Line2D(
                                new Point2D(l2.start.x, l2.start.y),
                                new Point2D(l2.velocity.x, l2.velocity.y)
                        )
                );
                if (crossed != null
                        //area checking
                        && crossed.x.compareTo(minArea) >= 0 && crossed.x.compareTo(maxArea) <= 0
                        && crossed.y.compareTo(minArea) >= 0 && crossed.y.compareTo(maxArea) <= 0
                        //positive time checking
                        && lines[i].isPointCanBeInLineXY(crossed)
                        && lines[j].isPointCanBeInLineXY(crossed)) {
                    crossedCount++;
                }
            }
        }
        return crossedCount;
    }

    public String findPositionForThrowingStone() {
        Point3D xy = findPerfectXYSpeed();
        return xy.x.add(xy.y).add(xy.z).setScale(0, RoundingMode.HALF_UP).toString();
    }

    private Point3D findPerfectXYSpeed() {
        for (int vx = -500; vx <= 500; vx++) {
            for (int vy = -500; vy <= 500; vy++) {
                //Search velocity for XY
                BigDecimal stoneVelocityX = new BigDecimal(vx);
                BigDecimal stoneVelocityY = new BigDecimal(vy);
                Point3D stoneVelocity = new Point3D(stoneVelocityX, stoneVelocityY, BigDecimal.ZERO);

                Line3D l1 = lines[0];
                Line3D l2 = lines[4];
                Point2D crossed = cross2D(
                        new Line2D(l1.start.get2D(Point3D::getX, Point3D::getY),
                                l1.velocity.subtract(stoneVelocity).get2D(Point3D::getX, Point3D::getY)
                        ),
                        new Line2D(
                                l2.start.get2D(Point3D::getX, Point3D::getY),
                                l2.velocity.subtract(stoneVelocity).get2D(Point3D::getX, Point3D::getY)
                        )
                );
                if (crossed == null) {
                    continue;
                }

                boolean match = true;
                for (Line3D line : lines) {
                    if (!line.shiftForVelocity(stoneVelocity).isPointCanBeInLineXY(crossed)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    //Search velocity for Z
                    for (int vz = -500; vz <= 500; vz++) {
                        BigDecimal stoneVelocityZ = new BigDecimal(vz);
                        stoneVelocity = new Point3D(stoneVelocityX, stoneVelocityY, stoneVelocityZ);

                        l1 = lines[0];
                        l2 = lines[4];
                        Point2D zPoint = cross2D(
                                new Line2D(
                                        l1.start.get2D(Point3D::getX, Point3D::getZ),
                                        l1.velocity.subtract(stoneVelocity).get2D(Point3D::getX, Point3D::getZ)
                                ),
                                new Line2D(
                                        l2.start.get2D(Point3D::getX, Point3D::getZ),
                                        l2.velocity.subtract(stoneVelocity).get2D(Point3D::getX, Point3D::getZ)
                                )
                        );
                        if (zPoint == null) {
                            continue;
                        }
                        Point3D fullCross = new Point3D(crossed.x, crossed.y, zPoint.y);
                        boolean matchZ = true;
                        for (Line3D line : lines) {
                            if (!line.shiftForVelocity(stoneVelocity).isPointCanBeInLineXYZ(fullCross)) {
                                matchZ = false;
                                break;
                            }
                        }
                        if (matchZ) {
                            return fullCross;
                        }
                    }
                }
            }
        }
        throw new RuntimeException();
    }

    private Point2D cross2D(Line2D l1, Line2D l2) {
        BigDecimal xy1 = l2.start.x.multiply(l2.start.y.add(l2.velocity.y)).subtract(l2.start.y.multiply(l2.start.x.add(l2.velocity.x)));
        BigDecimal xy2 = l1.start.x.multiply(l1.start.y.add(l1.velocity.y)).subtract(l1.start.y.multiply(l1.start.x.add(l1.velocity.x)));
        BigDecimal x = l1.velocity.x.multiply(xy1).subtract(l2.velocity.x.multiply(xy2));
        BigDecimal y = l1.velocity.y.multiply(xy1).subtract(l2.velocity.y.multiply(xy2));

        BigDecimal div = l1.velocity.x.multiply(l2.velocity.y).subtract(l1.velocity.y.multiply(l2.velocity.x));
        if (div.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return new Point2D(x.divide(div, 6, RoundingMode.HALF_EVEN), y.divide(div, 6, RoundingMode.HALF_EVEN));
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new HailCollider("day24/test.txt").countCollides2D(
                new BigDecimal("7"),
                new BigDecimal("27")
        ));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new HailCollider("day24/input.txt").countCollides2D(
                new BigDecimal("200000000000000"),
                new BigDecimal("400000000000000")
        ));

        System.out.println("== TEST 2 ==");
        System.out.println(new HailCollider("day24/test.txt").findPositionForThrowingStone());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new HailCollider("day24/input.txt").findPositionForThrowingStone());
    }

    private static class Line2D {
        private final Point2D start;
        private final Point2D velocity;

        public Line2D(Point2D start, Point2D velocity) {
            this.start = start;
            this.velocity = velocity;
        }
    }

    private static class Line3D {
        private final Point3D start;
        private final Point3D velocity;

        public Line3D(String s) {
            String[] split = s.split(" +@ +");
            this.start = new Point3D(split[0]);
            this.velocity = new Point3D(split[1]);
        }

        public Line3D(Point3D start, Point3D velocity) {
            this.start = start;
            this.velocity = velocity;
        }

        public Line3D shiftForVelocity(Point3D shiftVelocity) {
            return new Line3D(start, velocity.subtract(shiftVelocity));
        }

        public boolean isPointCanBeInLineXYZ(Point3D p) {
            Optional<BigDecimal> xTime = getTime(pp -> pp.x, p.x);
            if (xTime == null) {
                return false;
            }
            if (xTime.isPresent() && xTime.get().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }

            Optional<BigDecimal> yTime = getTime(pp -> pp.y, p.y);
            if (yTime == null) {
                return false;
            }
            if (yTime.isPresent() && yTime.get().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }

            Optional<BigDecimal> zTime = getTime(pp -> pp.z, p.z);
            if (zTime == null) {
                return false;
            }
            if (zTime.isPresent() && zTime.get().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }

            if (xTime.isEmpty() && yTime.isEmpty() && zTime.isEmpty()) {
                return true;
            } else if (xTime.isEmpty()) {
                return yTime.isEmpty() || zTime.isEmpty() || yTime.get().compareTo(zTime.get()) == 0;
            } else if (yTime.isEmpty()) {
                return zTime.isEmpty() || xTime.get().compareTo(zTime.get()) == 0;
            } else if (zTime.isEmpty()) {
                return xTime.get().compareTo(yTime.get()) == 0;
            } else {
                return xTime.get().compareTo(zTime.get()) == 0 && yTime.get().compareTo(zTime.get()) == 0;
            }
        }

        public boolean isPointCanBeInLineXY(Point2D p) {
            Optional<BigDecimal> xTime = getTime(pp -> pp.x, p.x);
            if (xTime == null) {
                return false;
            }
            if (xTime.isPresent() && xTime.get().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }

            Optional<BigDecimal> yTime = getTime(pp -> pp.y, p.y);
            if (yTime == null) {
                return false;
            }
            if (yTime.isPresent() && yTime.get().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
            return xTime.isEmpty() || yTime.isEmpty() || xTime.get().compareTo(yTime.get()) == 0;
        }

        private Optional<BigDecimal> getTime(Function<Point3D, BigDecimal> coord, BigDecimal dest) {
            BigDecimal p = coord.apply(start);
            BigDecimal v = coord.apply(velocity);
            if (v.compareTo(BigDecimal.ZERO) == 0) {
                if (p.compareTo(dest) == 0) {
                    return Optional.empty();
                } else {
                    return null;
                }
            } else {
                return Optional.of(dest.subtract(p).divide(v, 3, RoundingMode.HALF_EVEN));
            }
        }
    }

    private static class Point3D {
        BigDecimal x;
        BigDecimal y;
        BigDecimal z;

        public Point3D(String s) {
            String[] split = s.split(", *");
            this.x = new BigDecimal(new BigInteger(split[0]));
            this.y = new BigDecimal(new BigInteger(split[1]));
            this.z = new BigDecimal(new BigInteger(split[2]));
        }

        public Point3D subtract(Point3D p) {
            return new Point3D(x.subtract(p.x), y.subtract(p.y), z.subtract(p.z));
        }

        public Point2D get2D(Function<Point3D, BigDecimal> xMap, Function<Point3D, BigDecimal> yMap) {
            return new Point2D(xMap.apply(this), yMap.apply(this));
        }

        public Point3D(BigDecimal x, BigDecimal y, BigDecimal z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public BigDecimal getX() {
            return x;
        }

        public BigDecimal getY() {
            return y;
        }

        public BigDecimal getZ() {
            return z;
        }
    }

    private static class Point2D {
        BigDecimal x;
        BigDecimal y;

        public Point2D(BigDecimal x, BigDecimal y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point2D point2D = (Point2D) o;
            return Objects.equals(x, point2D.x) && Objects.equals(y, point2D.y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
