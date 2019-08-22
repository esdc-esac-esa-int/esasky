package esac.archive.esasky.cl.wcstransform.module.jskycoords;

import java.io.Serializable;

public abstract class Point2D {

    /**
     * The <code>Double</code> class defines a point specified in <code>double</code> precision.
     * @since 1.2
     */
    public static class Double extends Point2D implements Serializable {

        /**
         * The X coordinate of this <code>Point2D</code>.
         * @since 1.2
         * @serial
         */
        public double x;

        /**
         * The Y coordinate of this <code>Point2D</code>.
         * @since 1.2
         * @serial
         */
        public double y;

        /**
         * Constructs and initializes a <code>Point2D</code> with coordinates (0,&nbsp;0).
         * @since 1.2
         */
        public Double() {
        }

        /**
         * Constructs and initializes a <code>Point2D</code> with the specified coordinates.
         *
         * @param x the X coordinate of the newly constructed <code>Point2D</code>
         * @param y the Y coordinate of the newly constructed <code>Point2D</code>
         * @since 1.2
         */
        public Double(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
        public double getX() {
            return x;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
        public double getY() {
            return y;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
        public void setLocation(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Returns a <code>String</code> that represents the value of this <code>Point2D</code>.
         * @return a string representation of this <code>Point2D</code>.
         * @since 1.2
         */
        @Override
        public String toString() {
            return "Point2D.Double[" + x + ", " + y + "]";
        }

        /*
         * JDK 1.6 serialVersionUID
         */
        private static final long serialVersionUID = 6150783262733311327L;
    }

    /**
     * This is an abstract class that cannot be instantiated directly. Type-specific implementation
     * subclasses are available for instantiation and provide a number of formats for storing the
     * information necessary to satisfy the various accessor methods below.
     *
     * @see java.awt.geom.Point2D.Float
     * @see java.awt.geom.Point2D.Double
     * @see java.awt.Point
     * @since 1.2
     */
    protected Point2D() {
    }

    /**
     * Returns the X coordinate of this <code>Point2D</code> in <code>double</code> precision.
     * @return the X coordinate of this <code>Point2D</code>.
     * @since 1.2
     */
    public abstract double getX();

    /**
     * Returns the Y coordinate of this <code>Point2D</code> in <code>double</code> precision.
     * @return the Y coordinate of this <code>Point2D</code>.
     * @since 1.2
     */
    public abstract double getY();

    /**
     * Sets the location of this <code>Point2D</code> to the specified <code>double</code>
     * coordinates.
     *
     * @param x the new X coordinate of this {@code Point2D}
     * @param y the new Y coordinate of this {@code Point2D}
     * @since 1.2
     */
    public abstract void setLocation(double x, double y);

}
