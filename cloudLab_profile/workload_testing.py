"""testing workload to prove our assumptions and hypothesis for project Tarsonis"""

#
# NOTE: This code was machine converted. An actual human would not
#       write code like this!
#

# Import the Portal object.
import geni.portal as portal
# Import the ProtoGENI library.
import geni.rspec.pg as pg
# Import the Emulab specific extensions.
import geni.rspec.emulab as emulab

# Create a portal object,
pc = portal.Context()

# Create a Request object to start building the RSpec.
request = pc.makeRequestRSpec()

# Node cpu
node_cpu = request.RawPC('cpu')
node_cpu.hardware_type = 'm510'
node_cpu.disk_image = 'urn:publicid:IDN+emulab.net+image+emulab-ops//UBUNTU18-64-STD'
iface0 = node_cpu.addInterface('interface-0', pg.IPv4Address('192.168.1.0','255.255.255.0'))
iface1 = node_cpu.addInterface('interface-2', pg.IPv4Address('192.168.1.2','255.255.255'))

# Node memory
node_memory = request.RawPC('memory')
node_memory.hardware_type = 'm510'
node_memory.disk_image = 'urn:publicid:IDN+emulab.net+image+emulab-ops//UBUNTU18-64-STD'
iface2 = node_memory.addInterface('interface-3', pg.IPv4Address('192.168.1.3','255.255.255.0'))

# Node storage
node_storage = request.RawPC('storage')
node_storage.hardware_type = 'm510'
node_storage.disk_image = 'urn:publicid:IDN+emulab.net+image+emulab-ops//UBUNTU18-64-STD'
iface3 = node_storage.addInterface('interface-1', pg.IPv4Address('192.168.1.1','255.255.255.0'))

# Link link_cpu_storage
link_cpu_storage = request.Link('link_cpu_storage')
link_cpu_storage.Site('undefined')
link_cpu_storage.addInterface(iface0)
link_cpu_storage.addInterface(iface3)

# Link link_cpu_memory
link_cpu_memory = request.LAN('link_cpu_memory')
link_cpu_memory.setNoInterSwitchLinks()
link_cpu_memory.Site('undefined')
link_cpu_memory.addInterface(iface1)
link_cpu_memory.addInterface(iface2)


# Print the generated rspec
pc.printRequestRSpec(request)