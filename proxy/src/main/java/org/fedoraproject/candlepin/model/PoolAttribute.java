/**
 * Copyright (c) 2009 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */

package org.fedoraproject.candlepin.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 * See Attributes interface for documentation.
 */
@Entity
@Table(name = "cp_pool_attribute")
@Embeddable
@SequenceGenerator(name = "seq_pool_attribute", sequenceName = "seq_pool_attribute",
    allocationSize = 1)
public class PoolAttribute extends AbstractHibernateObject implements Attribute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pool_attribute")
    protected Long id;

    @Column(nullable = false)
    protected String name;

    @Column
    protected String value;

    @ManyToOne
    private Pool pool;
    
    public PoolAttribute() {
    }

    public PoolAttribute(String name, String quantity) {
        this.name = name;
        this.value = quantity;
    }

    @XmlTransient
    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public String toString() {
        return "PoolAttribute [id=" + id + ", name=" + name + ", value=" + value + "]";
    }

    public String getName() {
        return name;
    }

    @XmlTransient
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (!(anObject instanceof Attribute)) {
            return false;
        }

        Attribute another = (Attribute) anObject;

        return
            name.equals(another.getName()) &&
            value.equals(another.getValue());
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + value.hashCode();
    }

}