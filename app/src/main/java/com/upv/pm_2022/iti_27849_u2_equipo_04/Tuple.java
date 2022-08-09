package com.upv.pm_2022.iti_27849_u2_equipo_04;

public class Tuple {
    public int node_id;
    public boolean requestedLink;
    public Tuple(int node_id, boolean requestedLink) {
        this.node_id = node_id; this.requestedLink = requestedLink;
    }
    public Tuple(int node_id) { this(node_id, false); }
}
