require 'spec/expectations'
require 'candlepin_api'

Given /^consumer "([^\"]*)" consumes an entitlement for the "([^\"]*)" product$/ do |consumer_name, product|
   Given "I am logged in as \"#{@username}\""
   
   consumer = @current_owner_cp.register(consumer_name, :system)
   @consumer_clients[consumer_name] = connect(nil, nil, consumer['idCert']['cert'], consumer['idCert']['key'])
   @consumer_clients[consumer_name].consume_product(product.hash.abs)
end

When /^I regenerate entitlement certificates for "([^\"]*)" product$/ do |product|
  @old_certs ||= []
  
  @consumers.each do |consumer|
    @old_certs.concat(consumer.list_certificates())
  end

  @current_owner_cp.regenerate_entitlement_certificates_for_product(product.hash.abs)  
end

Then /^consumers have new entitlement certificates$/ do
  new_certs = []
  @consumers.each do |consumer|
    new_certs.concat(consumer.list_certificates())
  end
  
  @old_certs.size.should == new_certs.size
  old_ids = @old_certs.map { |cert| cert['serial']['id']}
  new_ids = new_certs.map { |cert| cert['serial']['id']}
  (old_ids & new_ids).size.should == 0  
end

Then /^consumer "([^\"]*)" has (\d+) entitlement certificates?$/ do |consumer_name, count|
  @consumer_clients[consumer_name].list_certificates.length.should == count.to_i
end

# Deprecated
Before do
    @serials = []
end

# TODO: this test should actually be using ?serials=x,y,z to test serial filtering
# server side, not client side:
When /^I filter certificates on the serial number for "([^\"]*)"$/ do |entitlement|
    certificates = @consumer_cp.list_certificates()
    found = certificates.find {|item|
        ent = @consumer_cp.get_entitlement(item['entitlement']['id'])
        pool = @consumer_cp.get_pool(ent['pool']['id'])
        pool['productId'] == entitlement.hash.abs.to_s}
    @serials << found['serial']['id']
end

Then /^I have (\d+) filtered certificate[s]?$/ do |certificates_size|
    @consumer_cp.list_certificates(@serials).length.should == certificates_size.to_i
end

Then /^the filtered certificates are revoked$/ do
  @serials.each { |serial| @candlepin.get_serial(serial)['revoked'].should == true }
end

Then /^the filtered certificates are not revoked$/ do
  @serials.each { |serial| @candlepin.get_serial(serial)['revoked'].should == false }
end

Then /^the filtered certificates are in the CRL$/ do
  @serials.each { |serial| revoked_serials.should include(serial) }
end

Then /^the filtered certificates are not in the CRL$/ do
  @serials.each { |serial| revoked_serials.should_not include(serial) }
end

When /^I regenerate all my entitlement certificates$/ do
  @old_certs = @consumer_cp.list_certificates()
  @consumer_cp.regenerate_entitlement_certificates()
end

Then /^I have new entitlement certificates$/ do
  new_certs = @consumer_cp.list_certificates()
  @old_certs.size.should == new_certs.size
  old_ids = @old_certs.map { |cert| cert['serial']['id']}
  new_ids = new_certs.map { |cert| cert['serial']['id']}
  (old_ids & new_ids).size.should == 0
end

def revoked_serials
  @candlepin.get_crl.revoked.collect { |entry| entry.serial }
end
