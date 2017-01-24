#include <stdio.h>
#include <string.h>

#include "defines.h"
#include "enums.h"
#include "protein.h"
#include "topology.h"
#include "pdbio.h"
#include "pdbatom.h"
#include "futil.h"
#include "messages.h"
#include "string_owner.h"

int test_begining_chi(char* line) {
    return strncmp(line,"ROOT",4) == 0 || strncmp(line,"BRANCH",6) == 0;
}

int test_ending_chi(char* line) {
    return strncmp(line,"ENDROOT",7) == 0 || strncmp(line,"ENDBRANCH",9) == 0;
}

int test_atom(char* line) {
    return strncmp(line,"ATOM",4) == 0;
}

void load_pdbqt_file(pdb_atom_t *atoms, top_residue_atom_info_t *chis,
        const char *fname) {
//    FILE *pdbfile=NULL;
//    char pdbqt_contents [MAX_LINE_PDB][MAX_LINE_PDB];
//    int branch_table[128][2];
//    int atom_table[128];
//    int n_atoms = -1;
//    int n_fixed_atoms = -1;
//    int n_moved_atoms = -1;
//    int table_cursor = 0;
//    int table_max = -1;
//    int line_atm = -1;
//    int line_max = -1;
//    int line_cursor1 = 0;
//    int line_cursor2 = -1;
//    int depth_level = -1;
//    
//    pdbfile = open_file(fname, fREAD);
//    while (fgets(pdbqt_contents[line_cursor1++],MAX_LINE_PDB,pdbfile) != NULL);
//    line_max = line_cursor1;
//    
//    for (line_cursor1 = 0; line_cursor1 < line_max; line_cursor1++) {
//        if (test_begining_chi(pdbqt_contents[line_cursor1])) {
//            line_cursor2 = line_cursor1;
//            depth_level = 0;
//            for (;;) {
//                if (test_begining_chi(pdbqt_contents[line_cursor2])) {
//                    depth_level++;
//                }
//                else if (test_ending_chi(pdbqt_contents[line_cursor2])) {
//                    if (depth_level == 0) {
//                        break;
//                    }
//                    depth_level--;
//                }
//            }
//            branch_table[table_cursor][0] = line_cursor1;
//            branch_table[table_cursor][1] = line_cursor2;
//            table_cursor++;
//        }
//    }
//    table_max = table_cursor;
//    chis = Malloc(top_residue_atom_info_t, table_max);
//    
//    for (line_cursor1 = 0; line_cursor1 < line_max; line_cursor1++) {
//        if (test_atom(pdbqt_contents[line_cursor1])) {
//            line_atm++;
//            load_pdb_atoms(pdbqt_contents[line_cursor1],atoms,&line_atm);
//            atom_table[line_cursor1] = line_atm;
//        }
//    }
//    n_atoms = line_atm;
//    
//    for (table_cursor = 0; table_cursor < table_max; table_cursor++) {
//        n_fixed_atoms = branch_table[table_cursor][1]
//                      - branch_table[table_cursor][0];
//        n_moved_atoms = n_atoms - n_fixed_atoms;
//        
//        chis[table_cursor].num_fixed = n_fixed_atoms;
//        chis[table_cursor].num_moved = n_moved_atoms;
//        chis[table_cursor].fixed_atoms = Malloc(int, n_fixed_atoms);
//        chis[table_cursor].moved_atoms = Malloc(int, n_moved_atoms)
//        
//        line_atm = 0;
//        for (line_cursor1 = 0; line_cursor1 < line_max; line_cursor1++) {
//            if (line_cursor1 > branch_table[table_cursor][0] + 1
//                    && branch_table[table_cursor][1] < line_cursor1) {
//                chis[table_cursor].fixed_atoms[line_atm++] =
//                        atom_table[line_cursor1];
//            } else if (test_atom(pdbqt_contents[line_cursor1])) {
//                chis[ta]
//            }
//        }
//    }
//    
//    for (table_cursor = 0; table_cursor < table_max; table_cursor++) {
//        printf("%d\t%d\n", branch_table[table_cursor][0],
//                branch_table[table_cursor][1]);
//    }
//    
//    fclose(pdbfile);
}