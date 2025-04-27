public class Light {
    private float[] pos;
    private final float intensity;
    float[] color = new float[]{1, 1, 1};
    boolean turnOn = true;

    public Light(float[] pos, float strength) {
        this.pos = pos;
        intensity = strength;
    }

    public Light(float[] pos, float strength, float[] color) {
        this.pos = pos;
        intensity = strength / (float) (4 * Math.PI);
        this.color = color;
    }

    public boolean needLight(Hero hero){
        return hero.needRenderLight(pos) && turnOn;
    }

    public void setHeroPos(Hero hero) {
        this.pos = Vector3.add(hero.getPos(),new float[]{0, 0.2f, 0});
    }

    public int useLight(Intersection inter, float AMBIENT) {
        float[] v = Vector3.subtract(pos,inter.getPoint());
        if (Vector3.dot(inter.getNormal(),v) < 0) {
            return ColorOperation.mulColor(inter.color, AMBIENT);
        } else {
            float mag = Vector3.length(v);
            float lightIntensity = intensity / (mag * mag);
            float d = Math.max(
                    Vector3.dot(inter.getNormal(),Vector3.scale(Vector3.normalize(v),lightIntensity)),
                    //inter.getNormal().normalize().dot(v.normalize().scale(lightIntensity)),
                    AMBIENT
            );
            return ColorOperation.mulColor(ColorOperation.mulColor(inter.color, d), color);
        }
    }
}
