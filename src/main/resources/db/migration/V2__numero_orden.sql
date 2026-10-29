-- La secuencia es atómica; los huecos por rollback no reutilizan números.
CREATE SEQUENCE orden_numero_seq START WITH 5 INCREMENT BY 1 NOCYCLE;
