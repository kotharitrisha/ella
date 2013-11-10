# Dropbox email analyzer
import csv
from csv import DictReader

# Constants
PROD_ID = "prod_category_id"
title = "machine_id,site_session_id,domain_id,event_date,event_time,prod_category_id,prod_name,prod_qty,prod_totprice,basket_tot,hoh_most_education,census_region,household_size,hoh_oldest_age,household_income,children,racial_background,connection_speed,country_of_origin,zip_code,domain_name"

# Function
def read_my_file(filename):
    with open(filename, 'rb') as fh:
        cread = DictReader(fh)
        categories = {}
        first_done = False
        print "Users to target: "
        for row_info in cread:
            value = row_info[PROD_ID]
            if (value not in categories.keys()):
                categories[value] = [row_info]
            else:
                val = categories[value]
                val.append(row_info)
                categories[value] = val
        
        for key in categories.iterkeys():
            filename = "category_" + key + ".csv"
            print key
            #print categories[key]
            with open(filename, 'wb') as f:
                writer = csv.writer(f)
                first_done = False
                for l in categories[key]:
                    #print l
                    if (not first_done):
                        first_row = [x for x in l.keys()]
                        first_done = True
                        writer.writerow(first_row)
                    vals = []
                    str = ""
                    for k in l.iterkeys():
                        print k
                        print l[k]
                        str += (l[k])
                        #print k
                        vals.append(l[k])
                    writer.writerow(vals)

        print "------"

# Python note: This if statement checks that we're 
# running the script and not importing it.
if __name__ == '__main__':
    inputfile = 'AmazonWRDS.csv'
    read_my_file(inputfile)
